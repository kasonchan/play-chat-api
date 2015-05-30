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
   * Return the user(s) if there is/are match(es)
   * Otherwise return not found messages
   * @param q: String
   * @return Future[JsValue]
   */
  private def findQuery(q: JsValue): Future[JsValue] = {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = usersCollection
      .find(q)
      .cursor[JsObject]

    // Gather all the JsObjects in a Seq
    val futureUsersList: Future[Seq[JsObject]] = cursor.collect[Seq]()

    // If the Seq is empty, return not found
    // Otherwise, return the Seq in Json format
    futureUsersList.map { users =>
      if (users.isEmpty) {
        Json.obj("messages" -> Json.arr("Not found :("))
      }
      else {
        Json.toJson(users)
      }
    }
  }

  /**
   * Extract user
   * Call printUser to remove the password field
   * Return Some(user) if a user is extracted
   * Otherwise, return None
   * @param jsValue JsValue
   * @return Option[JsValue]
   */
  private def extractUser(jsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (jsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val users = jsValue.as[JsArray]
        val response = printUser(users(0))
        Some(response)
    }
  }

  /**
   * Extract users
   * Call printUser to remove the password field
   * Return Some(users) if users are extracted
   * Otherwise return None
   * @param jsValue JsValue
   * @return Option[JsValue]
   */
  private def extractUsers(jsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (jsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val users: JsArray = jsValue.as[JsArray]
        val usersSeq = users.value.map { user =>
          printUser(user)
        }
        val response = new JsArray(usersSeq)
        Some(response)
    }
  }

  /**
   * Print user
   * Extract the user's information except the password for printing
   * @param jsValue JsValue
   * @return JsValue
   */
  private def printUser(user: JsValue): JsValue = {
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
   * Find by login
   * Find the users' login by target
   * Call findQuery to find matched user login in the database
   * Call extract user to get user from the query result
   * Return Ok if a user found
   * Otherwise return NotFound
   * @param target String
   * @return Action[AnyContent]
   */
  def findByLogin(target: String): Action[AnyContent] = Action.async {
    // Execute findQuery function to access the database to find the login
    val q = Json.obj("login" -> target)
    val futureJsValue: Future[JsValue] = findQuery(q)

    futureJsValue.map { jsValue =>
      // Execute extractUser to extract the user from the query result
      val js = extractUser(jsValue)

      js match {
        case Some(user) =>
          Logger.info(user.toString)
          Ok(prettify(user)).as("application/json; charset=utf-8")
        case None =>
          Logger.info(jsValue.toString())
          NotFound(prettify(jsValue)).as("application/json; charset=utf-8")
      }
    }
  }

  /**
   * Find the user by login and password
   * Call findQuery to find matched user login and password in the database
   * Call extract user to get user from the query result
   * Return Ok if a user found
   * Otherwise return Unauthorized with bad credentials message
   * @param u String
   * @param p String
   * @return Future[Result]
   */
  def findByLoginAndPassword(u: String, p: String): Future[Option[JsValue]] = {
    // Execute findQuery function to access the database to find the login and
    // password
    val q = Json.obj("login" -> u, "password" -> p)
    val futureJsValue: Future[JsValue] = findQuery(q)

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
   * Find all users
   * List all the users in the database
   * Call findQuery to get all the users in the database
   * Call extractUsers to extract users if any
   * Return Ok if there are users found
   * Otherwise return NotFound
   * @return
   */
  def findAll: Action[AnyContent] = Action.async {
    // Execute findQuery function to access the database to find the login and
    // password
    val q = Json.obj()
    val futureJsValue: Future[JsValue] = findQuery(q)

    futureJsValue.map {
      jsValue =>
        // Execute extractUser to extract the user from the query result
        val js = extractUsers(jsValue)

        js match {
          case Some(users) =>
            Logger.info(users.toString)
            Ok(prettify(users)).as("application/json; charset=utf-8")
          case None =>
            Logger.info(jsValue.toString())
            NotFound(prettify(jsValue)).as("application/json; charset=utf-8")
        }
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
  def getAuthUser: Action[AnyContent] = Action.async {
    request =>
      // Get the authorization header
      val authorization: Option[String] = request.headers.get(AUTHORIZATION)

      // If authorization can be extracted, decode with Basic AuthScheme
      // Call findByLoginAndPassword to check if the user is stored in db
      // Return Ok with the user info if the user is valid
      // Return Unauthorized with bad credentials if the user is not found
      // Otherwise return Unauthorized with requires authentication message
      authorization match {
        case Some(a) =>
          val encoded: Option[String] = a.split(" ").drop(1).headOption
          encoded match {
            case Some(e) =>
              val decoded: Array[String] = new String(decodeBase64(e.getBytes)).split(":")
              val authorizedFuture: Future[Option[JsValue]] =
                findByLoginAndPassword(decoded(0).toString, decoded(1).toString)

              authorizedFuture.map { authorized =>
                authorized match {
                  case Some(user) =>
                    Logger.info(user.toString)
                    Ok(prettify(user)).as("application/json; charset=utf-8")
                  case None =>
                    val response = Json.obj("messages" -> Json.arr("Bad credentials"))
                    Logger.info(response.toString())
                    Unauthorized(prettify(response)).as("application/json; charset=utf-8")
                }
              }
            case None =>
              val response = Json.obj("messages" -> Json.arr("Bad credentials"))
              Logger.info(response.toString())
              Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
          }
        case None =>
          val response = Json.obj("messages" -> Json.arr("Requires authentication"))
          Logger.info(response.toString())
          Future.successful(Unauthorized(prettify(response)).as("application/json; charset=utf-8"))
      }
  }

  /**
   * Create
   * Parse json from post request
   * If the user's login and email is already registered return BadRequest
   * Otherwise insert the new user to the database and return Ok with the new
   * user
   * @return Action[JsValue]
   */
  def create: Action[JsValue] = Action.async(parse.json) {
    request =>

      val transformer: Reads[JsObject] =
        Reads.jsPickBranch[JsString](__ \ "login") and
          Reads.jsPickBranch[JsString](__ \ "avatar_url") and
          Reads.jsPickBranch[JsString](__ \ "type") and
          Reads.jsPickBranch[JsString](__ \ "email") and
          Reads.jsPickBranch[JsString](__ \ "location") and
          Reads.jsPickBranch[JsString](__ \ "password") and
          Reads.jsPickBranch[JsBoolean](__ \ "confirmed") and
          Reads.jsPickBranch[JsNumber](__ \ "created_at") and
          Reads.jsPickBranch[JsNumber](__ \ "updated_at") reduce

      val transformedResult: JsValue = request.body.transform(transformer).map {
        tr =>
          tr
      }.getOrElse {
        val response: JsValue = Json.obj("messages" -> Json.arr("Invalid Json"))
        response
      }

      // Retrieve the information from the parsed json and validate it
      val validatedResult: Option[JsValue] = validateUser(transformedResult)

      validatedResult match {
        case Some(resultJs) => {
          val response: JsValue = Json.obj("messages" -> resultJs)
          Logger.info(response.toString())
          Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
        }
        case None => {
          val login: String = (transformedResult \ "login").as[String]
          val email: String = (transformedResult \ "email").as[String]

          // Check if the new user is already registered
          // Extract the user from query result
          // If the user is already registered, return BadRequest messages
          // Otherwise insert the new user to the database
          // Call printUser to extract the new user information except password
          // Return Ok with the new user
          val l = Json.obj("login" -> login)
          val queryLoginResultFuture: Future[JsValue] = findQuery(l)
          val e = Json.obj("email" -> email)
          val queryEmailResultFuture: Future[JsValue] = findQuery(e)

          val result: Future[Result] =
            queryLoginResultFuture.zip(queryEmailResultFuture).map {
              case (qlr, qer) => {
                val queryLoginResult: Option[JsValue] = extractUser(qlr)
                val queryEmailResult: Option[JsValue] = extractUser(qer)

                (queryLoginResult, queryEmailResult) match {
                  case (Some(l), Some(e)) => {
                    val response: JsValue =
                      Json.obj("messages" -> Json.arr("Login is already registered",
                        "Email is already registered"))
                    Logger.info(response.toString())
                    BadRequest(prettify(response)).as("application/json; charset=utf-8")
                  }
                  case (Some(l), None) => {
                    val response: JsValue =
                      Json.obj("messages" -> Json.arr("Login is already registered"))
                    Logger.info(response.toString())
                    BadRequest(prettify(response)).as("application/json; charset=utf-8")
                  }
                  case (None, Some(e)) => {
                    val response: JsValue =
                      Json.obj("messages" -> Json.arr("Email is already registered"))
                    Logger.info(response.toString())
                    BadRequest(prettify(response)).as("application/json; charset=utf-8")
                  }
                  case (None, None) => {
                    usersCollection.insert(transformedResult).map {
                      r => Created
                    }
                    val pu = printUser(transformedResult)
                    Logger.info(pu.toString())
                    Status(201)(prettify(pu)).as("application/json; charset=utf-8")
                  }
                }
              }
            }

          result
        }
      }
  }

}
