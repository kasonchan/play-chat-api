package controllers

import json.JSON
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
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
   * Find the user by name
   * @param name String
   * @return Action[AnyContent]
   */
  def find(name: String): Action[AnyContent] = Action.async {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = usersCollection
      .find(Json.obj("name" -> name))
      .cursor[JsObject]

    // Gather all the JsObjects in a Seq
    val futureUsersList: Future[Seq[JsObject]] = cursor.collect[Seq]()

    // If the Seq is empty, return not found
    // Otherwise, return the Seq in Json format
    futureUsersList.map { users =>
      if (users.isEmpty) {
        val response: JsValue = Json.obj("message" -> "Not found")
        NotFound(prettify(response))
      }
      else {
        // Ok(users.mkString("[", ",", "]"))
        val usersInJson: JsValue = Json.toJson(users)
        Ok(prettify(usersInJson))
      }
    }
  }

}
