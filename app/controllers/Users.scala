package controllers

import json.JSON
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller, Result}
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
   * @param login String
   * @return Action[AnyContent]
   */
  def find(login: String): Action[AnyContent] = Action.async {
    // Execute findHelper function to access the db and find the login
    findHelper(login)
  }

  /**
   * Find helper
   * Access the db and find the login
   * If the user is found, return the user
   * Otherwise return not found
   * @param login String
   * @return Future[Result]
   */
  private def findHelper(login: String): Future[Result] = {
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
        val response: JsValue = Json.obj("message" -> "Not found :(")
        NotFound(prettify(response)).as("application/json; charset=utf-8")
      }
      else {
        // Ok(users.mkString("[", ",", "]"))
        val usersInJson: JsValue = Json.toJson(users)
        Ok(prettify(usersInJson)).as("application/json; charset=utf-8")
      }
    }
  }

  def create = Action.async(parse.json) { request =>

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

    request.body.transform(transformer).map { transformedResult =>
      usersCollection.insert(transformedResult).map { result =>
        Created
      }
      Future.successful(Ok(prettify(transformedResult))
        .as("application/json; charset=utf-8"))
    }
      .getOrElse {
      val response: JsValue = Json.obj("message" -> "Bad request")
      Future.successful(BadRequest(prettify(response))
        .as("application/json; charset=utf-8"))
    }
  }

}
