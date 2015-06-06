package applicationtest

import json.JSON
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/6/15.
 */
object Get extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "GET / must be None" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "")
      val response = route(request)
      response must beNone
    }
  }

  "GET /none must be None" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/none")
      val response = route(request)
      response must beNone
    }
  }

  "GET /api/v0.1 must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("current_user_url" -> "/api/v0.1/user",
          "user_url" -> "/api/v0.1/users/{user}")

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 200
    }
  }

}
