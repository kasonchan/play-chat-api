package test

import json.JSON
import play.api.libs.json.Json
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 5/29/15.
 */
class RoomsTest extends PlaySpecification with JSON {
  val timeout: FiniteDuration = 10.seconds

  "GET /api/v0.1/rooms must be 401" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Requires authentication"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

}
