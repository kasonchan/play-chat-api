package controllers

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController

import scala.concurrent.Future

object Application extends Controller with MongoController {

  def root = Action.async {
    Future.successful(NotFound("The requested resource could not be found."))
  }

}