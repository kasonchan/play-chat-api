package controllers

import json.JSON
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import play.modules.reactivemongo.MongoController

import scala.concurrent.Future

object Application extends Controller with MongoController with JSON {

  /**
   * Root
   * Return all api links
   * @return Action[AnyContent]
   */
  def root: Action[AnyContent] = Action.async {
    val response: JsValue =
      Json.obj("current_user_url" -> "/api/v0.1/user",
      "user_url" -> "/api/v0.1/users/{user}")
    Future.successful(Ok(prettify(response)).as("application/json; charset=utf-8"))
  }

}