package controllers

import controllers.Users.findByLoginAndPassword
import json.JSON
import org.apache.commons.codec.binary.Base64.decodeBase64
import play.api.Logger
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
 * Created by ka-son on 5/27/15.
 */
object Rooms extends Controller with MongoController with JSON {

  /**
   * Rooms collection
   * Connect to the rooms collection
   * @return JSONCollection
   */
  def roomsCollection: JSONCollection = db.collection[JSONCollection]("rooms")

  /**
   * Find query
   * Access the database and find with the query
   * Return the room(s) if there is/are match(es)
   * Otherwise return not found messages
   * @param q: String
   * @return Future[JsValue]
   */
  private def findQuery(q: JsValue): Future[JsValue] = {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = roomsCollection
      .find(q)
      .cursor[JsObject]

    // Gather all the JsObjects in a Seq
    val futureRoomsList: Future[Seq[JsObject]] = cursor.collect[Seq]()

    // If the Seq is empty, return not found
    // Otherwise, return the Seq in Json format
    futureRoomsList.map { rooms =>
      if (rooms.isEmpty) {
        Json.obj("messages" -> Json.arr("Not found"))
      }
      else {
        Json.toJson(rooms)
      }
    }
  }

  /**
   * Extract rooms
   * Return Some(rooms) if rooms are extracted
   * Otherwise return None
   * @param jsValue JsValue
   * @return Option[JsValue]
   */
  private def extractRooms(jsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (jsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val rooms: JsArray = jsValue.as[JsArray]
        val roomsSeq = rooms.value.map { room =>
          printRoom(room)
        }
        val response = new JsArray(roomsSeq)
        Some(response)
    }
  }

  /**
   * Print room
   * Extract the room's information
   * @param room JsValue
   * @return JsValue
   */
  private def printRoom(room: JsValue): JsValue = {
    // Extract room's information except the password
    val login = (room \ "login").as[String]
    val avatar_url = (room \ "avatar_url").as[String]
    val users = (room \ "users").as[Seq[String]]
    val privacy = (room \ "privacy").as[String]
    val created_at = (room \ "created_at").as[Long]
    val updated_at = (room \ "updated_at").as[Long]

    val response = Json.obj("login" -> login,
      "avatar_url" -> avatar_url,
      "users" -> users,
      "privacy" -> privacy,
      "created_at" -> created_at,
      "updated_at" -> updated_at)

    response
  }

  /**
   * Find all rooms by username
   * List all the rooms in the database that contains the username
   * Return Ok if there are rooms found
   * Otherwise return NotFound
   * @return Future[Result]
   */
  def findAllByUser(user: String): Future[Result] = {
    // Execute findQuery function to access the database to find the login and
    // password
    val q = Json.obj({
      "users" -> user
    })
    val futureJsValue: Future[JsValue] = findQuery(q)

    futureJsValue.map { jsValue =>
      // Execute extractUser to extract the room from the query result
      val js: Option[JsValue] = extractRooms(jsValue)

      js match {
        case Some(rooms) =>
          val response: JsValue = rooms
          Logger.info(response.toString())
          Ok(prettify(response)).as("application/json; charset=utf-8")
        case None =>
          val response = Json.obj("messages" -> Json.arr("Not found"))
          Logger.info(response.toString())
          NotFound(prettify(response)).as("application/json; charset=utf-8")
      }
    }
  }

  def findAllByLoginAndUser(login: String, user: String): Future[Option[JsValue]] = {
    // Execute findQuery function to access the database to find the login and
    // password
    val q = Json.obj({
      "login" -> login
      "users" -> user
    })
    val futureJsValue: Future[JsValue] = findQuery(q)

    futureJsValue.map { jsValue =>
      // Execute extractUser to extract the room from the query result
      val js: Option[JsValue] = extractRooms(jsValue)

      js match {
        case Some(rooms) => Some(rooms)
        case None => None
      }
    }
  }

  /**
   * Find all rooms by authenticated user
   * If the user is authenticated, and rooms are found, return Ok with rooms
   * If no rooms are found, returns NotFound
   * Otherwise returns Unauthorized with bad credentials
   * @param decoded Array[String]
   * @return Future[Result]
   */
  def findAllByLoginPasswordAndUser(decoded: Array[String]): Future[Result] = {
    val authorizedFuture: Future[Option[JsValue]] =
      findByLoginAndPassword(decoded(0).toString, decoded(1).toString)
    val roomsFuture: Future[Result] = findAllByUser(decoded(0).toString)

    authorizedFuture.zip(roomsFuture).map {
      case (Some(a: JsValue), r) =>
        r
      case (None, r) =>
        val response = Json.obj("messages" -> Json.arr("Bad credentials"))
        Logger.info(response.toString())
        Unauthorized(prettify(response)).as("application/json; charset=utf-8")
    }
  }

  /**
   * Get authorized
   * If authenticated, decode the the login and password
   * If login and password are not decoded, return bad credentials message
   * If no authorization, Returns requires authentication message
   * @param authorization Option[String]
   * @return Product with Serializable
   */
  def getAuthorized(authorization: Option[String]): Product with Serializable = {
    authorization match {
      case Some(a) =>
        val encoded: Option[String] = a.split(" ").drop(1).headOption

        encoded match {
          case Some(e) =>
            val decoded: Array[String] = new String(decodeBase64(e.getBytes)).split(":")
            // Login and password
            Some(decoded)
          case None =>
            Json.obj("messages" -> Json.arr("Bad credentials"))
        }
      case None =>
        Json.obj("messages" -> Json.arr("Requires authentication"))
    }
  }

  /**
   * Get rooms with the authenticated user
   * If authorization can be extracted, decode with Basic AuthScheme
   * Call findByLoginAndPassword to check if the user is stored in db
   * Call findAllByUser with the decoded username
   * Return Ok with the user info if the user is valid and rooms list
   * Return NotFound if the user is valid but no room found
   * Return Unauthorized with bad credentials if the user is not found
   * Otherwise return Unauthorized with requires authentication message
   * @return Action[AnyContent]
   */
  def getRooms: Action[AnyContent] = Action.async { request =>

    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    // If authorization can be extracted, decode with Basic AuthScheme
    // Call findByLoginAndPassword to check if the user is stored in db
    // Call findAllByUser with the decoded username
    // Return Ok with the user info if the user is valid and rooms list
    // Return NotFound if the user is valid but no room found
    // Return Unauthorized with bad credentials if the user is not found
    // Otherwise return Unauthorized with requires authentication message

    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        findAllByLoginPasswordAndUser(decoded)
      case (jv: JsValue) =>
        Logger.info(jv.toString)
        Future.successful(Unauthorized(prettify(jv)).as("application/json; charset=utf-8"))
    }
  }

}
