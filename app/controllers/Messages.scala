package controllers

import controllers.Users.{findByLoginAndPassword, findByLogins}
import json.JSON
import org.apache.commons.codec.binary.Base64.decodeBase64
import play.api.Logger
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import validations.RoomValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
 * Created by ka-son on 6/7/15.
 */
object Messages extends Controller with MongoController with JSON {

  /**
   * Messages collection
   * Connect to the messages collection
   * @return JSONCollection
   */
  def messagesCollection: JSONCollection = db.collection[JSONCollection]("messages")

  /**
   * Find query
   * Find the query q from the messages collection
   * Return messages if found in descending order
   * Otherwise return Not found
   * @param q JsValue
   * @return Future[JsValue]
   */
  private def queryFind(q: JsValue): Future[JsValue] = {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = messagesCollection
      .find(q)
      .sort(Json.obj("created_at" -> -1))
      .cursor[JsObject]

    // Gather all the JsObjects in a Seq
    val futureRoomsList: Future[Seq[JsObject]] = cursor.collect[Seq]()

    // If the Seq is empty, return not found
    // Otherwise, return the Seq in Json format
    futureRoomsList.map { messages =>
      if (messages.isEmpty) {
        Json.obj("messages" -> Json.arr("Not found"))
      }
      else {
        Json.toJson(messages)
      }
    }
  }

  /**
   * Extract messages
   * If error messages is found return None
   * Otherwise return all the messages
   * @param jsValue JsValue
   * @return Option[JsValue]
   */
  private def extractMessages(jsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (jsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val messages: JsArray = jsValue.as[JsArray]
        val messagesSeq: Seq[JsValue] = messages.value.map { message =>
          messagePrinting(message)
        }
        val response = new JsArray(messagesSeq)
        Some(response)
    }
  }

  /**
   * Print the message
   * Extract the message information
   * @param message JsValue
   * @return JsValue
   */
  private def messagePrinting(message: JsValue): JsValue = {
    // Extract room's information except the password
    val owner = (message \ "owner").as[String]
    val users = (message \ "users").as[Seq[JsObject]]
    val coordinates = (message \ "coordinates").as[JsObject]
    val text = (message \ "text").as[String]
    val created_at = (message \ "created_at").as[Long]

    val response = Json.obj("owner" -> owner,
      "users" -> users,
      "coordinates" -> coordinates,
      "text" -> text,
      "created_at" -> created_at)

    response
  }

  /**
   * Find the messages by owner
   * Query find messages by the owner
   * Return Some(messages) if found
   * Otherwise return None
   * @param user String
   * @return Future[Option[JsValue]]
   */
  def findByOwner(user: String): Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the login and
    // password
    val q = Json.obj(
      "owner" -> user
    )
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map { jsValue =>
      // Execute extractRooms to extract the room from the query result
      val js: Option[JsValue] = extractMessages(jsValue)

      js match {
        case Some(messages) => Some(messages)
        case None => None
      }
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
   * Find messages by username
   * Check for authorization
   * If authorization is not found, return requires authentication message
   * Otherwise check if authorization username is equal to get username
   * If they are not the same, return bad credentials
   * Otherwise, check for login and password
   * If they are invalid, return bad credentials
   * Otherwise find the messages by owner
   * If messages are found, return messages
   * Otherwise, return not found
   * @param username String
   * @return Action[AnyContent]
   */
  def find(username: String): Action[AnyContent] = Action.async { request =>
    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        try {
          if (decoded(0) == username) {

            val authorizedFuture: Future[Option[JsValue]] =
              findByLoginAndPassword(decoded(0).toString, decoded(1).toString)
            val queryResultFuture: Future[Option[JsValue]] = findByOwner(decoded(0))

            authorizedFuture zip queryResultFuture map {
              case (Some(user), Some(messages)) =>
                val response = messages
                Logger.info(response.toString())
                Ok(prettify(response)).as("application/json; charset=utf-8")
              case (Some(user), None) =>
                val response = Json.obj("messages" -> Json.arr("Not found"))
                Logger.info(response.toString())
                NotFound(prettify(response)).as("application/json; charset=utf-8")
              case (None, _) =>
                // Invalid user
                val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                Logger.info(response.toString())
                Unauthorized(prettify(response)).as("application/json; charset=utf-8")
            }
          }
          else {
            val response = Json.obj("messages" -> Json.arr("Bad credentials"))
            Logger.info(response.toString())
            Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
          }
        } catch {
          case e: Exception =>
            val response = Json.obj("messages" -> Json.arr("Bad credentials"))
            Logger.info(response.toString())
            Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
        }
      case (jv: JsValue) =>
        Logger.info(jv.toString())
        Future.successful(Unauthorized(prettify(jv)).as("application/json; charset=utf-8"))
    }
  }

}
