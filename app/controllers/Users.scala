package controllers

import json.JSON
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by kasonchan on 5/20/15.
 */
object Users extends Controller with MongoController with JSON {

  def usersCollection: JSONCollection = db.collection[JSONCollection]("users")

  /**
   * Find
   * Find the user by login
   * Call findQuery to find in the database
   * Call extract user to get user from the query result
   * @param login String
   * @return Action[AnyContent]
   */
  def find(login: String): Action[AnyContent] = Action.async {

    // Execute findQuery function to access the database to find the login
    val futureJsValue: Future[JsValue] = findQuery(login)

    futureJsValue.map { jsValue =>
      // Execute extractUser to extract the user from the query result
      val js = extractUser(jsValue)

      js match {
        case Some(user) => Ok(prettify(user)).as("application/json; charset=utf-8")
        case None => NotFound(prettify(jsValue)).as("application/json; charset=utf-8")
      }
    }
  }

  /**
   * Find query
   * Access the database and find the login
   * If the login is matched, return the user(s)
   * Otherwise return not found message
   * @param login String
   * @return Future[JsValue]
   */
  def findQuery(login: String): Future[JsValue] = {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = usersCollection
      .find(Json.obj("login" -> login))
      .cursor[JsObject]

    // Gather all the JsObjects in a Seq
    val futureUsersList: Future[Seq[JsObject]] = cursor.collect[Seq]()

    // If the Seq is empty, return not found
    // Otherwise, return the Seq in Json format
    futureUsersList.map { users =>
      if (users.isEmpty) {
        Json.obj("message" -> "Not found :(")
      }
      else {
        // Ok(users.mkString("[", ",", "]"))
        Json.toJson(users)
      }
    }
  }

  /**
   * Extract user
   * Return Some(user) if a user is extracted
   * Otherwise, return None
   * @param jsValue JsValue
   * @return Option[JsValue]
   */
  def extractUser(jsValue: JsValue): Option[JsValue] = {
    // Extract message string if any
    val jv = (jsValue \ "message").asOpt[String]

    jv match {
      case Some(s) => None
      case None =>
        val users = jsValue.as[JsArray]
        val response = printUser(users(0))
        Some(response)
    }
  }

  /**
   * Print user
   * Extract the user's information except the password for printing
   * @param jsValue JsValue
   * @return JsValue
   */
  def printUser(user: JsValue): JsValue = {
    // Extract user's information except the password
    val login = (user \ "login").as[String]
    val avatar = (user \ "avatar").as[String]
    val user_type = (user \ "type").as[String]
    val email = (user \ "email").as[String]
    val location = (user \ "location").as[String]
    val confirmed = (user \ "confirmed").as[Boolean]
    val created_at = (user \ "created_at").as[Long]
    val updated_at = (user \ "updated_at").as[Long]

    val response = Json.obj("login" -> login,
      "avatar" -> avatar,
      "type" -> user_type,
      "email" -> email,
      "location" -> location,
      "confirmed" -> confirmed,
      "created_at" -> created_at,
      "updated_at" -> updated_at)

    response
  }

  /**
   * Create
   * Parse json from post request
   * Check if the user is already registered return error message
   * Otherwise insert the new user to the database and return the new user
   * @return Action[JsValue]
   */
  def create: Action[JsValue] = Action.async(parse.json) {
    request =>

      val transformer: Reads[JsObject] =
        Reads.jsPickBranch[JsString](__ \ "login") and
          Reads.jsPickBranch[JsString](__ \ "avatar") and
          Reads.jsPickBranch[JsString](__ \ "type") and
          Reads.jsPickBranch[JsString](__ \ "email") and
          Reads.jsPickBranch[JsString](__ \ "location") and
          Reads.jsPickBranch[JsString](__ \ "password") and
          Reads.jsPickBranch[JsBoolean](__ \ "confirmed") and
          Reads.jsPickBranch[JsNumber](__ \ "created_at") and
          Reads.jsPickBranch[JsNumber](__ \ "updated_at") reduce

      request.body.transform(transformer).map {
        transformedResult =>

          // Retrieve the login string from the parsed json
          val login = (transformedResult \ "login").as[String]

          // Check if the new user is already registered
          // Extract the user from query result
          // If the user is already registered, return the message
          // Otherwise insert the new user to the database
          // Call printUser to extract the new user information except password
          val queryResult = findQuery(login)

          queryResult.map {
            qr =>
              val result = extractUser(qr)

              result match {
                case None =>
                  usersCollection.insert(transformedResult).map {
                    r =>
                      Created
                  }
                  val pu = printUser(transformedResult)
                  Ok(prettify(pu)).as("application/json; charset=utf-8")
                case Some(user) =>
                  val response = Json.obj("message" -> "Email is already registered")
                  BadRequest(response).as("application/json; charset=utf-8")
              }
          }
      }.getOrElse {
        val response: JsValue = Json.obj("message" -> "Bad request")
        Future.successful(BadRequest(prettify(response))
          .as("application/json; charset=utf-8"))
      }
  }

}
