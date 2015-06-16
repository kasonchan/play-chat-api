package controllers

import controllers.Rooms.findByUsers
import controllers.Users.{findByLoginAndPassword, findByLogins}
import json.JSON
import org.apache.commons.codec.binary.Base64.decodeBase64
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import validations.MessageValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
 * Created by ka-son on 6/7/15.
 */
object Messages extends Controller with MongoController with JSON with MessageValidation {

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
    val users = (message \ "users").as[Seq[String]]
    val reads = (message \ "reads").as[Seq[JsObject]]
    val coordinates = (message \ "coordinates").as[JsObject]
    val text = (message \ "text").as[String]
    val created_at = (message \ "created_at").as[Long]
    val updated_at = (message \ "updated_at").as[Long]

    val response = Json.obj("owner" -> owner,
      "users" -> users,
      "reads" -> reads,
      "coordinates" -> coordinates,
      "text" -> text,
      "created_at" -> created_at,
      "updated_at" -> updated_at)

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

  /**
   * Transform users
   * Add read to each user
   * Add true if it is owner
   * Add false otherwise
   * @param owner String
   * @param users Seq[String]
   * @return JsValue
   */
  def transformCreateUsers(owner: String, users: Seq[String]): JsValue = {
    val usersArray: Seq[JsObject] = users.map { user =>
      if (user == owner) Json.obj("login" -> user, "read" -> true)
      else Json.obj("login" -> user, "read" -> false)
    }

    Json.toJson(usersArray)
  }

  /**
   * Create a new message
   * @return
   */
  def create: Action[JsValue] = Action.async(parse.json) { request =>
    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    // Check for authentication
    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        // Valid authentication

        val transformer: Reads[JsObject] = Reads.jsPickBranch[JsString](__ \ "owner") and
          Reads.jsPickBranch[JsArray](__ \ "users") and
          Reads.jsPickBranch[JsString](__ \ "text") reduce

        // Transform the json format
        val transformedResult: Option[JsValue] =
          request.body.transform(transformer).map { tr =>
            // Valid json format
            Some(tr)
          }.getOrElse {
            // Invalid json format
            None
          }

        transformedResult match {
          case Some(tr: JsValue) =>
            // Valid json format

            // Check the individual users are registered
            // Validate room
            try {
              val validatedMessage: Option[JsValue] = validateMessage(tr)

              validatedMessage match {
                case Some(s: JsValue) =>
                  val response = Json.obj("messages" -> Json.arr(s))
                  Logger.info(response.toString)
                  Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
                case None =>
                  val owner: String = (tr \ "owner").as[String]

                  if (decoded(0) == owner) {

                    // Check if the user is authorized
                    val authorizedFuture: Future[Option[JsValue]] =
                      findByLoginAndPassword(decoded(0).toString(), decoded(1).toString())

                    val users: Seq[String] = (tr \ "users").asOpt[Seq[String]].getOrElse(Seq())
                    val text: String = (tr \ "text").as[String]
                    val coordinates: JsValue =
                      (request.body \ "coordinates").asOpt[JsValue]
                        .getOrElse(Json.obj("coordinates" -> Json.obj()))

                    // Sort the users
                    val sortedUsers: Seq[String] = users.sortWith(_ < _)
                    // Check if the room of users is already existed
                    val roomFuture: Future[Option[JsValue]] = findByUsers(sortedUsers)

                    // Check all the users are valid
                    val usersFuture: Future[Option[String]] = findByLogins(sortedUsers)

                    authorizedFuture.zip(usersFuture).zip(roomFuture).map {
                      case ((Some(authorized: JsValue), None), Some(users)) =>
                        // Authorized, valid users, room existed
                        // Create a new message
                        val message = Json.obj(
                          "owner" -> decoded(0),
                          "users" -> sortedUsers,
                          "reads" -> transformCreateUsers(decoded(0), sortedUsers),
                          "text" -> text,
                          "coordinates" -> coordinates,
                          "created_at" -> System.currentTimeMillis(),
                          "updated_at" -> System.currentTimeMillis()
                        )

                        // Insert the message into the db
                        messagesCollection.insert(message).map {
                          r => Created
                        }
                        val pu = messagePrinting(message)
                        Logger.info(pu.toString())
                        Status(201)(prettify(pu)).as("application/json; charset=utf-8")
                      case ((Some(authorized), None), None) =>
                        // Authorized, valid users, room do not exist
                        val response = Json.obj("messages" -> Json.arr("Not found"))
                        Logger.info(response.toString())
                        NotFound(prettify(response)).as("application/json; charset=utf-8")
                      case ((Some(authorized), Some(users)), _) =>
                        // Authorized, invalid users
                        val response = Json.obj("messages" -> Json.arr("Invalid users"))
                        Logger.info(response.toString())
                        BadRequest(prettify(response)).as("application/json; charset=utf-8")
                      case ((None, _), _) =>
                        // Not authorized
                        val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                        Logger.info(response.toString())
                        Unauthorized(prettify(response)).as("application/json; charset=utf-8")
                    }
                  } else {
                    val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                    Logger.info(response.toString())
                    Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
                  }
              }
            } catch {
              case e: Exception =>
                val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                Logger.info(response.toString())
                Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
            }
          case None =>
            // Invalid json format
            val response: JsValue = Json.obj("messages" -> Json.arr("Invalid Json"))
            Logger.info(response.toString())
            Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
        }
      case (jv: JsValue) =>
        // Unauthorized bad credentials or requires authentication
        Logger.info(jv.toString)
        Future.successful(Unauthorized(prettify(jv)).as("application/json; charset=utf-8"))
    }
  }

  /**
   * Find the messages by the room
   * @param users Seq[String]
   * @return Future[Option[JsValue]]
   */
  def findByRoom(users: Seq[String]): Future[Option[JsValue]] = {
    val q = Json.obj("users" -> users)
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map { jsValue =>
      // Execute extractUser to extract the room from the query result
      val js: Option[JsValue] = extractMessages(jsValue)

      js match {
        case Some(messages) => Some(messages)
        case None => None
      }
    }
  }

  def getMessages: Action[JsValue] = Action.async(parse.json) { request =>
    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    // Check for authentication
    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        // Valid authentication

        val users: Option[Seq[String]] = (request.body \ "users").asOpt[Seq[String]]

        users match {
          case Some(users: Seq[String]) =>
            // Valid json format
            try {
              // Check if the user is authorized
              val authorizedFuture: Future[Option[JsValue]] =
                findByLoginAndPassword(decoded(0).toString(), decoded(1).toString())

              // Sort the users
              val sortedUsers: Seq[String] = users.sortWith(_ < _)
              // Check if the room of users is already existed
              val roomFuture: Future[Option[JsValue]] = findByUsers(sortedUsers)

              // Find all messages by room
              val messagesFuture: Future[Option[JsValue]] = findByRoom(sortedUsers)

              authorizedFuture.zip(roomFuture).zip(messagesFuture).map {
                case ((Some(authorized), Some(room)), Some(messages)) =>
                  // Authorized, room exists, messages exists
                  if (users.contains(decoded(0))) {
                    val response = messages
                    Logger.info(response.toString())
                    Ok(prettify(response)).as("application/json; charset=utf-8")
                  } else {
                    // Not authorized
                    val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                    Logger.info(response.toString())
                    Unauthorized(prettify(response)).as("application/json; charset=utf-8")
                  }
                case ((Some(authorized), Some(room)), None) =>
                  // Authorized, room exists, no messages
                  val response = Json.obj("messages" -> Json.arr("Not found"))
                  Logger.info(response.toString())
                  NotFound(prettify(response)).as("application/json; charset=utf-8")
                case ((Some(authorized), None), _) =>
                  // Authorized, room not exists
                  val response = Json.obj("messages" -> Json.arr("Not found"))
                  Logger.info(response.toString())
                  NotFound(prettify(response)).as("application/json; charset=utf-8")
                case ((None, _), _) =>
                  // Not authorized
                  val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                  Logger.info(response.toString())
                  Unauthorized(prettify(response)).as("application/json; charset=utf-8")
              }
            } catch {
              case e: Exception =>
                val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                Logger.info(response.toString())
                Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
            }
          case None =>
            // Invalid json format
            val response: JsValue = Json.obj("messages" -> Json.arr("Invalid Json"))
            Logger.info(response.toString())
            Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
        }
      case (jv: JsValue) =>
        // Unauthorized bad credentials or requires authentication
        Logger.info(jv.toString)
        Future.successful(Unauthorized(prettify(jv)).as("application/json; charset=utf-8"))
    }
  }

}
