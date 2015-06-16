package messagestest

import json.JSON
import play.api.libs.json.Json
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/12/15.
 */
object PostGet extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  /**
   * Get messages by room
   */
  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b" ] } """.stripMargin +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "users": [ "a", "b" ] } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b" ] } """.stripMargin +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "users": [ "a", "b" ] } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

}
