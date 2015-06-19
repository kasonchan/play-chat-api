package controllers

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
import reactivemongo.core.commands.LastError
import validations.UserValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
 * Created by kasonchan on 5/20/15.
 */
object Users extends Controller with MongoController with JSON with UserValidation {

  /**
   * Users collection
   * Connect to the users collection
   * @return JSONCollection
   */
  def usersCollection: JSONCollection = db.collection[JSONCollection]("users")

  /**
   * Find query
   * Access the database and find the target
   * Sort the users in ascending order
   * Return the user(s) if there is/are match(es)
   * Otherwise return not found messages
   * @param q: String
   * @return Future[JsValue]
   */
  private def queryFind(q: JsValue): Future[JsValue] = {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = usersCollection
      .find(q)
      .sort(Json.obj("login" -> 1))
      .cursor[JsObject]

    // Gather all the JsObjects in a Seq
    val futureUsersList: Future[Seq[JsObject]] = cursor.collect[Seq]()

    // If the Seq is empty, return not found
    // Otherwise, return the Seq in Json format
    futureUsersList.map { users =>
      if (users.isEmpty) {
        Json.obj("messages" -> Json.arr("Not found"))
      }
      else {
        Json.toJson(users)
      }
    }
  }

  /**
   * Extract user
   * Call userPrinting to remove the password field
   * Return Some(user) if a user is extracted
   * Otherwise, return None
   * @param userJsValue JsValue
   * @return Option[JsValue]
   */
  private def extractUser(userJsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (userJsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val users = userJsValue.as[JsArray]
        val response = userPrinting(users(0))
        Some(response)
    }
  }

  /**
   * Extract users
   * Call userPrinting to remove the password field
   * Return Some(users) if users are extracted
   * Otherwise return None
   * @param usersJsValue JsValue
   * @return Option[JsValue]
   */
  def extractUsers(usersJsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (usersJsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val users: JsArray = usersJsValue.as[JsArray]
        val usersSeq = users.value.map { user =>
          userPrinting(user)
        }
        val response = new JsArray(usersSeq)
        Some(response)
    }
  }

  /**
   * Print user
   * Extract the user's information except the password for printing
   * @param user JsValue
   * @return JsValue
   */
  private def userPrinting(user: JsValue): JsValue = {
    // Extract user's information except the password
    val login = (user \ "login").as[String]
    val avatar_url = (user \ "avatar_url").as[String]
    val user_type = (user \ "type").as[String]
    val email = (user \ "email").as[String]
    val location = (user \ "location").as[String]
    val confirmed = (user \ "confirmed").as[Boolean]
    val created_at = (user \ "created_at").as[Long]
    val updated_at = (user \ "updated_at").as[Long]

    val response = Json.obj("login" -> login,
      "avatar_url" -> avatar_url,
      "type" -> user_type,
      "email" -> email,
      "location" -> location,
      "confirmed" -> confirmed,
      "created_at" -> created_at,
      "updated_at" -> updated_at)

    response
  }

  /**
   * Find
   * Find the user by login
   * @param target String
   * @return Action[AnyContent]
   */
  def find(target: String): Action[AnyContent] = Action.async {
    val queryResult: Future[Option[JsValue]] = findByLogin(target)

    queryResult.map {
      case Some(user: JsValue) =>
        Logger.info(user.toString)
        Ok(prettify(user)).as("application/json; charset=utf-8")
      case None =>
        val response = Json.obj("messages" -> Json.arr("Not found"))
        Logger.info(response.toString)
        NotFound(prettify(response)).as("application/json; charset=utf-8")
    }
  }

  /**
   * Find by login
   * Find the users' login by target
   * Call queryFind to find matched user login in the database
   * Call extract user to get user from the query result
   * Return the user if a user found
   * Otherwise return None
   * @param target String
   * @return Future[Option[JsValue]]
   */
  def findByLogin(target: String): Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the login
    val q = Json.obj("login" -> target)
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map { jsValue =>
      // Execute extractUser to extract the user from the query result
      val js: Option[JsValue] = extractUser(jsValue)

      js match {
        case r@Some(user) => r
        case None => None
      }
    }
  }

  /**
   * Find users by logins
   * Find all the users by login to validate they are registered
   * Check every users in the sequence
   * Return None if all users are valid
   * Otherwiser return invalid users error messages
   * @param users Seq[String]
   * @return Future[Option[String]]
   */
  def findByLogins(users: Seq[String]): Future[Option[String]] = {
    // Execute queryFind function to access the database to find the login
    val resultSeq: Seq[Future[Option[JsValue]]] = users.map { user =>
      val q = Json.obj("login" -> user)

      val futureJsValue: Future[JsValue] = queryFind(q)

      futureJsValue.map { jsValue =>
        // Execute extractUser to extract the user from the query result
        val js: Option[JsValue] = extractUser(jsValue)

        // If user is found, return true
        // Otherwise return false
        js match {
          case Some(user: JsValue) => Some(user)
          case None => None
        }
      }
    }

    // Convert Seq[Future] to Future[Seq]
    val results: Future[Seq[Option[JsValue]]] = Future.sequence(resultSeq)

    results.map { r =>
      r.contains(None) match {
        case true => Some("Invalid users")
        case false => None
      }
    }
  }

  /**
   * Find by email
   * Find the users' email by target
   * Call queryFind to find matched user login in the database
   * Call extract user to get user from the query result
   * Return the user if a user found
   * Otherwise return None
   * @param target String
   * @return Future[Option[JsValue]]
   */
  def findByEmail(target: String): Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the email
    val q = Json.obj("email" -> target)
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map {
      jsValue =>
        // Execute extractUser to extract the user from the query result
        val js: Option[JsValue] = extractUser(jsValue)

        js match {
          case r@Some(user) => r
          case None => None
        }
    }
  }

  /**
   * Find the user by login and password
   * Call queryFind to find matched user login and password in the database
   * Call extract user to get user from the query result
   * Return Ok if a user found
   * Otherwise return Unauthorized with bad credentials message
   * @param u String
   * @param p String
   * @return Future[Result]
   */
  def findByLoginAndPassword(u: String, p: String): Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the login and
    // password
    val q = Json.obj(
      "login" -> u,
      "password" -> p
    )
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map {
      jsValue =>
        // Execute extractUser to extract the user from the query result
        val js: Option[JsValue] = extractUser(jsValue)

        js match {
          case r@Some(user) => r
          case None => None
        }
    }
  }

  /**
   * Find all users
   * List all the users who type is user only in the database
   * Return Ok if there are users found
   * Otherwise return NotFound
   * @return
   */
  def findAll: Action[AnyContent] = Action.async {
    // Execute queryFind function to access the database to find the login and
    // password
    val q = Json.obj("type" -> "user")
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map {
      jsValue =>
        // Execute extractUser to extract the user from the query result
        val js = extractUsers(jsValue)

        js match {
          case Some(users) =>
            Logger.info(users.toString)
            Ok(prettify(users)).as("application/json; charset=utf-8")
          case None =>
            Logger.info(jsValue.toString)
            NotFound(prettify(jsValue)).as("application/json; charset=utf-8")
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
        try {
          val encoded: Option[String] = a.split(" ").drop(1).headOption

          encoded match {
            case Some(e) =>
              val decoded: Array[String] = new String(decodeBase64(e.getBytes)).split(":")
              // Login and password
              Some(decoded)
            case None =>
              Json.obj("messages" -> Json.arr("Bad credentials"))
          }
        } catch {
          case e: Exception =>
            Json.obj("messages" -> Json.arr("Bad credentials"))
        }
      case None =>
        Json.obj("messages" -> Json.arr("Requires authentication"))
    }
  }

  /**
   * Get authorized user
   * If authorization can not be extracted from request, return Unauthorized
   * with requires authentication message
   * Otherwise extract authorization from header, decode with Basic AuthScheme
   * Query database with the credentials
   * If the user is exist, return Ok with the user info
   * Otherwise return Unauthorized with bad credentials message
   * @return Action[AnyContent]
   */
  def getAuthUser: Action[AnyContent] = Action.async { request =>
    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    // If authorization can be extracted, decode with Basic AuthScheme
    // Return Ok with the user info if the user is valid
    // Return Unauthorized with bad credentials if the user is not found
    // Otherwise return Unauthorized with requires authentication message
    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        try {
          val authorizedFuture: Future[Option[JsValue]] =
            findByLoginAndPassword(decoded(0).toString, decoded(1).toString)

          authorizedFuture.map {
            case Some(user) =>
              val login = (user \ "login").as[String]
              if (login == decoded(0)) {
                Logger.info(user.toString)
                Ok(prettify(user)).as("application/json; charset=utf-8")
              } else {
                val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                Logger.info(response.toString)
                Unauthorized(prettify(response)).as("application/json; charset=utf-8")
              }
            case None =>
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
        Logger.info(jv.toString)
        Future.successful(Unauthorized(prettify(jv)).as("application/json; charset=utf-8"))
    }
  }

  /**
   * Create
   * Parse json from post request
   * Return bad request if the json is invalid
   * Validate users inputs
   * If the user's login and email is already registered return BadRequest
   * Otherwise insert the new user to the database and return Ok with the new
   * user
   * @return Action[JsValue]
   */
  def create: Action[JsValue] = Action.async(parse.json) { request =>
    val transformer: Reads[JsObject] =
      Reads.jsPickBranch[JsString](__ \ "login") and
        Reads.jsPickBranch[JsString](__ \ "email") and
        Reads.jsPickBranch[JsString](__ \ "password") reduce

    // Transform the json format
    val transformedResult: Option[JsValue] =
      request.body.transform(transformer).map {
        tr =>
          // Valid json format
          Some(tr)
      }.getOrElse {
        // Invalid json format
        None
      }

    transformedResult match {
      case Some(tr: JsValue) =>
        // Valid json format
        // Check if login, email and password are valid input
        val validatedUser = validateUser(tr)

        validatedUser match {
          case Some(messages: JsValue) =>
            // Invalid input
            val response: JsValue = Json.obj("messages" -> messages)
            Logger.info(response.toString)
            Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
          case None =>
            // Valid input

            // Retrieve login, email and password
            val login: String = (tr \ "login").as[String]
            val email: String = (tr \ "email").as[String]
            val password: String = (tr \ "password").as[String]

            // Check if login and email are already registered
            val loginQueryResult = findByLogin(login)
            val emailQueryResult = findByEmail(email)

            loginQueryResult.zip(emailQueryResult).map {
              case (Some(lr: JsValue), Some(er: JsValue)) =>
                val response: JsValue =
                  Json.obj("messages" -> Json.arr("Login is already registered",
                    "Email is already registered"))
                Logger.info(response.toString)
                BadRequest(prettify(response)).as("application/json; charset=utf-8")
              case (Some(lr: JsValue), None) =>
                val response: JsValue =
                  Json.obj("messages" -> Json.arr("Login is already registered"))
                Logger.info(response.toString)
                BadRequest(prettify(response)).as("application/json; charset=utf-8")
              case (None, Some(er: JsValue)) =>
                val response: JsValue =
                  Json.obj("messages" -> Json.arr("Email is already registered"))
                Logger.info(response.toString)
                BadRequest(prettify(response)).as("application/json; charset=utf-8")
              case (None, None) =>
                // Create a new user
                val user = Json.obj(
                  "login" -> login,
                  "avatar_url" -> "",
                  "type" -> "user",
                  "email" -> email,
                  "location" -> "",
                  "password" -> password,
                  "confirmed" -> false,
                  "created_at" -> System.currentTimeMillis(),
                  "updated_at" -> System.currentTimeMillis()
                )

                // Insert the user into the db
                usersCollection.insert(user).map {
                  r => Created
                }
                val pu = userPrinting(user)
                Logger.info(pu.toString)
                Status(201)(prettify(pu)).as("application/json; charset=utf-8")
            }
        }
      case None =>
        // Invalid json format
        val response: JsValue = Json.obj("messages" -> Json.arr("Invalid Json"))
        Logger.info(response.toString)
        Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
    }
  }

  /**
   * Update query
   * Access the database and update the value
   * @param t String
   * @param u JsValue
   * @return Future[Option[String]]
   */
  def queryUpdate(t: String, u: JsValue): Future[Option[String]] = {
    val target = Json.obj("login" -> t)
    val update = Json.obj("$set" -> u)

    val cursor: Future[LastError] =
      usersCollection.update(target, update, multi = true)

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

          authorizedFuture.map {
            // Valid user
            case Some(user) =>
              val location = (request.body \ "location").asOpt[String]

              checkLocation(location) match {
                case Left(e) =>
                  // Invalid location
                  val response: JsValue = Json.obj("messages" -> Json.arr(e))
                  Logger.info(response.toString)
                  BadRequest(prettify(response)).as("application/json; charset=utf-8")
                case Right(l) =>
                  // Valid location
                  val update = Json.obj("location" -> l,
                    "updated_at" -> System.currentTimeMillis())
                  val result = queryUpdate(decoded(0), update)

                  result map {
                    case Some(e) =>
                      val response: JsValue = Json.obj("messages" -> Json.arr("Internal server error"))
                      Logger.error(e.toString)
                      InternalServerError(prettify(response)).as("application/json; charset=utf-8")
                    case None =>
                      find(decoded(0))
                  }
                  Logger.info(user.toString)
                  Ok(prettify(user)).as("application/json; charset=utf-8")
              }
            case None =>
              // Invalid user
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

  /**
   * Update confirmed
   * Access the database and update the field
   * @return Action[JsValue]
   */
  def updateConfirmed(user: String): Action[JsValue] = Action.async(parse.json) { request =>
    // Get the authorization header
    val authorization: Option[String] = request.headers.get(AUTHORIZATION)

    // Check for authentication
    getAuthorized(authorization) match {
      case Some(decoded: Array[String]) =>
        // Valid authentication
        try {
          if (decoded(0).toString == "playchatadmin") {
            val authorizedFuture: Future[Option[JsValue]] =
              findByLoginAndPassword(decoded(0).toString, decoded(1).toString)
            val queryFuture: Future[Option[JsValue]] = findByLogin(user)

            authorizedFuture zip queryFuture map {
              // Valid admin and user
              case (Some(a), Some(u)) =>
                val confirmed = (request.body \ "confirmed").asOpt[Boolean]

                checkConfirmed(confirmed) match {
                  case Some(e: String) =>
                    val response: JsValue = Json.obj("messages" -> Json.arr(e))
                    Logger.info(e.toString)
                    BadRequest(prettify(response)).as("application/json; charset=utf-8")
                  case None =>
                    val update = Json.obj("confirmed" -> confirmed,
                      "updated_at" -> System.currentTimeMillis())
                    val result = queryUpdate(user, update)

                    result map {
                      case Some(e) =>
                        val response: JsValue = Json.obj("messages" -> Json.arr("Internal server error"))
                        Logger.error(e.toString)
                        InternalServerError(prettify(response)).as("application/json; charset=utf-8")
                      case None =>
                        find(user)
                    }
                    Logger.info(u.toString)
                    Ok(prettify(u)).as("application/json; charset=utf-8")
                }
              case (Some(a), None) =>
                // Invalid user
                val response = Json.obj("messages" -> Json.arr("Not found"))
                Logger.info(response.toString)
                NotFound(prettify(response)).as("application/json; charset=utf-8")
              case (None, _) =>
                // Invalid admin
                val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                Logger.info(response.toString)
                Unauthorized(prettify(response)).as("application/json; charset=utf-8")
            }
          } else {
            val response = Json.obj("messages" -> Json.arr("Bad credentials"))
            Logger.info(response.toString)
            Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
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
