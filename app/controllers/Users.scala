package controllers

import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by kasonchan on 5/20/15.
 */
object Users extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("users")

  def find(name: String) = Action.async {
    // Do quert
    val cursor: Cursor[JsObject] = collection.find(Json.obj("name" -> name)).
      // Perform the query and get a cursor of JsObject
      cursor[JsObject]

    // Gather all the JsObjects in a list
    val futureUsersList: Future[List[JsObject]] = cursor.collect[List]()

    // Transform the list into a JsArray
    val futureUsersJsonArray: Future[JsArray] = futureUsersList.map { users =>
      Json.arr(users)
    }

    // Replay
    futureUsersJsonArray.map { users =>
      Ok(users)
    }
  }

}
