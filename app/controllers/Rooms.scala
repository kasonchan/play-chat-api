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
import reactivemongo.core.commands.LastError
import validations.RoomValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
 * Created by ka-son on 5/27/15.
 */
object Rooms extends Controller with MongoController with JSON with RoomValidation {

  /**
   * Rooms collection
   * Connect to the rooms collection
   * @return JSONCollection
   */
  def roomsCollection: JSONCollection = db.collection[JSONCollection]("rooms")

  /**
   * Find query
   * Access the database and find with the query
   * Sort the rooms in ascending order
   * Return the room(s) if there is/are match(es)
   * Otherwise return not found message
   * @param q: JsValue
   * @return Future[JsValue]
   */
  private def queryFind(q: JsValue): Future[JsValue] = {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = roomsCollection
      .find(q)
      .sort(Json.obj("login" -> 1, "created_at" -> 1))
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
          roomPrinting(room)
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
  private def roomPrinting(room: JsValue): JsValue = {
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
   * @param user String
   * @return Future[Result]
   */
  def findByUser(user: String): Future[Result] = {
    // Execute queryFind function to access the database to find the login and
    // password
    val q = Json.obj(
      "users" -> user
    )
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map { jsValue =>
      // Execute extractRooms to extract the room from the query result
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

  /**
   * Find all rooms by authenticated user
   * If the user is authenticated, and rooms are found, return Ok with rooms
   * If no rooms are found, returns NotFound
   * Otherwise returns Unauthorized with bad credentials
   * @param decoded Array[String]
   * @return Future[Result]
   */
  def findByLoginPasswordAndUser(decoded: Array[String]): Future[Result] = {
    val authorizedFuture: Future[Option[JsValue]] =
      findByLoginAndPassword(decoded(0).toString, decoded(1).toString)
    val roomsFuture: Future[Result] = findByUser(decoded(0).toString)

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
   * Call findByUser with the decoded username
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
    // Call findByUser with the decoded username
    // Return Ok with the user info if the user is valid and rooms list
    // Return NotFound if the user is valid but no room found
    // Return Unauthorized with bad credentials if the user is not found
    // Otherwise return Unauthorized with requires authentication message

    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        findByLoginPasswordAndUser(decoded)
      case (jv: JsValue) =>
        Logger.info(jv.toString())
        Future.successful(Unauthorized(prettify(jv)).as("application/json; charset=utf-8"))
    }
  }

  /**
   * Find all rooms by room login and user login
   * @param login String
   * @param user String
   * @return Future[Option[JsValue]]
   */
  def findByLoginAndUser(login: String, user: String): Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the login and
    // password
    val q = Json.obj(
      "users" -> user,
      "login" -> login
    )
    val futureJsValue: Future[JsValue] = queryFind(q)

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
   * Find the room by the users
   * @param users Seq[String]
   * @return Future[Option[JsValue]]
   */
  def findByUsers(users: Seq[String]): Future[Option[JsValue]] = {
    val q = Json.obj("users" -> users)
    val futureJsValue: Future[JsValue] = queryFind(q)

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
   * Create new room
   * Check authorization
   * Check json format
   * Validate users
   * Check if authorized
   * Check room is already created
   * Check all users are valid
   * @return Action[JsValue]
   */
  def create: Action[JsValue] = Action.async(parse.json) { request =>
    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    // Check for authentication
    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        // Valid authentication

        val transformer: Reads[JsObject] = Reads.jsPickBranch[JsArray](__ \ "users")

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
              val validatedRoom: Option[String] = validateRoom(decoded(0).toString(), tr)

              validatedRoom match {
                case Some(s: String) =>
                  val response = Json.obj("messages" -> Json.arr(s))
                  Logger.info(response.toString)
                  Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
                case None =>
                  // Check if the user is authorized
                  val authorizedFuture: Future[Option[JsValue]] =
                    findByLoginAndPassword(decoded(0).toString(), decoded(1).toString())

                  val users: Seq[String] = (tr \ "users").asOpt[Seq[String]].getOrElse(Seq())

                  // Sort the users
                  val sortedUsers = users.sortWith(_ < _)
                  // Check if the room of users is already existed
                  val roomFuture: Future[Option[JsValue]] = findByUsers(sortedUsers)

                  // Check all the users are valid
                  val usersFuture: Future[Option[String]] = findByLogins(sortedUsers)

                  authorizedFuture.zip(usersFuture).zip(roomFuture).map {
                    case ((Some(authorized: JsValue), Some(s: String)), Some(users)) =>
                      val response = Json.obj("messages" -> Json.arr(s, "Room is already created"))
                      Logger.info(response.toString())
                      BadRequest(prettify(response)).as("application/json; charset=utf-8")
                    case ((Some(authorized: JsValue), Some(s: String)), None) =>
                      val response = Json.obj("messages" -> Json.arr(s))
                      Logger.info(response.toString())
                      BadRequest(prettify(response)).as("application/json; charset=utf-8")
                    case ((Some(authorized: JsValue), None), Some(users)) =>
                      val response = Json.obj("messages" -> Json.arr("Room is already created"))
                      Logger.info(response.toString())
                      BadRequest(prettify(response)).as("application/json; charset=utf-8")
                    case ((Some(authorized: JsValue), None), None) =>
                      // Create a new room
                      val room = Json.obj(
                        "login" -> "",
                        "avatar_url" -> "",
                        "users" -> sortedUsers,
                        "privacy" -> "private",
                        "created_at" -> System.currentTimeMillis(),
                        "updated_at" -> System.currentTimeMillis()
                      )

                      // Insert the room into the db
                      roomsCollection.insert(room).map {
                        r => Created
                      }
                      val pu = roomPrinting(room)
                      Logger.info(pu.toString())
                      Status(201)(prettify(pu)).as("application/json; charset=utf-8")
                    case ((None, _), _) =>
                      val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                      Logger.info(response.toString())
                      Unauthorized(prettify(response)).as("application/json; charset=utf-8")
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
   * Find all rooms by username which are public
   * Return Some(rooms) if there are rooms found
   * Otherwise return None
   * @param user String
   * @return Future[Option[JsValue]]
   */
  def findByUserAndPublic(user: String): Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the login and
    // password
    val q = Json.obj(
      "users" -> user,
      "privacy" -> "public"
    )
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map { jsValue =>
      // Execute extractRooms to extract the room from the query result
      val js: Option[JsValue] = extractRooms(jsValue)

      js match {
        case Some(rooms) => Some(rooms)
        case None => None
      }
    }
  }

  /**
   * Find
   * Find all the rooms by username which are public
   * @param user String
   * @return Action[AnyContent]
   */
  def find(user: String): Action[AnyContent] = Action.async {
    val queryResult: Future[Option[JsValue]] = findByUserAndPublic(user)

    queryResult.map {
      case Some(rooms: JsValue) =>
        val response: JsValue = rooms
        Logger.info(response.toString())
        Ok(prettify(response)).as("application/json; charset=utf-8")
      case None =>
        val response = Json.obj("messages" -> Json.arr("Not found"))
        Logger.info(response.toString())
        NotFound(prettify(response)).as("application/json; charset=utf-8")
    }
  }

  /**
   * Find all rooms that are public
   * Return Some(rooms) if there are rooms found
   * Otherwise return None
   * @return Future[Option[JsValue]]
   */
  def findByPublic: Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the login and
    // password
    val q = Json.obj(
      "privacy" -> "public"
    )
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map { jsValue =>
      // Execute extractRooms to extract the room from the query result
      val js: Option[JsValue] = extractRooms(jsValue)

      js match {
        case Some(rooms) => Some(rooms)
        case None => None
      }
    }
  }

  /**
   * Find all public rooms
   * @return Action[AnyContent]
   */
  def findAll: Action[AnyContent] = Action.async {
    val queryResult: Future[Option[JsValue]] = findByPublic

    queryResult.map {
      case Some(rooms: JsValue) =>
        val response: JsValue = rooms
        Logger.info(response.toString())
        Ok(prettify(response)).as("application/json; charset=utf-8")
      case None =>
        val response = Json.obj("messages" -> Json.arr("Not found"))
        Logger.info(response.toString())
        NotFound(prettify(response)).as("application/json; charset=utf-8")
    }
  }

  /**
   * Update query
   * Access the database and update the value
   * @param t JsValue
   * @param u JsValue
   * @return Future[Option[String]]
   */
  def queryUpdate(t: Seq[String], u: JsValue): Future[Option[String]] = {
    val target = Json.obj("users" -> t)
    val update = Json.obj("$set" -> u)

    val cursor: Future[LastError] =
      roomsCollection.update(target, update, multi = true)

    cursor.map { l =>
      l.errMsg match {
        case Some(e) => Some(e)
        case None => None
      }
    }
  }

  /**
   * Update
   * Access the database and update the field
   * @return Action[JsValue]
   */
  def update: Action[JsValue] = Action.async(parse.json) { request =>
    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    // Check for authentication
    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        // Valid authentication

        try {
          val authorizedFuture: Future[Option[JsValue]] =
            findByLoginAndPassword(decoded(0).toString, decoded(1).toString)

          val users: Seq[String] = (request.body \ "users").asOpt[Seq[String]].getOrElse(Seq())

          // Sort the users
          val sortedUsers = users.sortWith(_ < _)
          // Check if the room of users is already existed
          val roomFuture: Future[Option[JsValue]] = findByUsers(sortedUsers)

          // Check all the users are valid
          val usersFuture: Future[Option[String]] = findByLogins(sortedUsers)

          authorizedFuture.zip(roomFuture).zip(usersFuture).map {
            case ((Some(a), Some(r)), None) =>
              // Valid auth, room exists, valid users
              val login: Option[String] = (request.body \ "login").asOpt[String]
              val privacy: Option[String] = (request.body \ "privacy").asOpt[String]

              val checkedLogin: Either[String, String] = checkLogin(login)
              val checkedPrivacy: Either[String, String] = checkPrivacy(privacy)

              (checkedLogin, checkedPrivacy) match {
                case (Left(l), Left(p)) =>
                  val response = Json.obj("messages" -> Json.arr(l, p))
                  Logger.info(response.toString)
                  BadRequest(prettify(response)).as("application/json; charset=utf-8")
                case (_, Left(p)) =>
                  val response = Json.obj("messages" -> Json.arr(p))
                  Logger.info(response.toString)
                  BadRequest(prettify(response)).as("application/json; charset=utf-8")
                case (Left(l), _) =>
                  val response = Json.obj("messages" -> Json.arr(l))
                  Logger.info(response.toString)
                  BadRequest(prettify(response)).as("application/json; charset=utf-8")
                case (Right(l), Right(p)) =>
                  val l = login.getOrElse("")
                  val p = privacy.getOrElse("private")

                  val update = Json.obj("login" -> l,
                    "privacy" -> p,
                    "updated_at" -> System.currentTimeMillis())
                  val resultFuture: Future[Option[String]] = queryUpdate(sortedUsers, update)

                  resultFuture map {
                    case Some(e) =>
                      val response: JsValue = Json.obj("messages" -> Json.arr("Internal server error"))
                      Logger.error(e.toString)
                      InternalServerError(prettify(response)).as("application/json; charset=utf-8")
                    case None =>
                      findByUsers(sortedUsers)
                  }

                  Logger.info(r.toString)
                  Ok(prettify(r)).as("application/json; charset=utf-8")
              }
            case ((Some(a), Some(r)), Some(u)) =>
              // Valid auth, room exists, invalid users
              val response = Json.obj("messages" -> Json.arr(u))
              Logger.info(response.toString)
              BadRequest(prettify(response)).as("application/json; charset=utf-8")
            case ((Some(a), None), _) =>
              // Room is not found
              val response = Json.obj("messages" -> Json.arr("Not found"))
              Logger.info(response.toString)
              NotFound(prettify(response)).as("application/json; charset=utf-8")
            case ((None, _), _) =>
              // Bad credentials
              val response = Json.obj("messages" -> Json.arr("Bad credentials"))
              Logger.info(response.toString)
              Unauthorized(prettify(response)).as("application/json; charset=utf-8")
          }
        } catch {
          case e: Exception =>
            val response = Json.obj("messages" -> Json.arr("Bad credentials"))
            Logger.info(response.toString)
            Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
        }
      case (jv: JsValue) =>
        // Unauthorized bad credentials or requires authentication
        Logger.info(jv.toString)
        Future.successful(Unauthorized(prettify(jv)).as("application/json; charset=utf-8"))
    }
  }

}
