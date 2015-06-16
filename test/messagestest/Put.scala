package messagestest

import json.JSON
import play.api.libs.json.Json
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/16/15.
 */
object Put extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "PUT /api/v0.1/user/room/messages " +
    """{"users": ["a", "b"]} """ +
    "must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Expecting text/json or application/json body"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/user/room/messages " +
    """{"users": ["a", "b"]} """ +
    "must be 401 Unauthorized Requires authentication" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room/messages")
        .withJsonBody(Json.parse( """{"users": ["a", "b"]}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Requires authentication"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "PUT /api/v0.1/user/room/messages " +
    """{"users": ["a", "b"]} """ +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0"))
        .withJsonBody(Json.parse( """{"users": ["a", "b"]}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "PUT /api/v0.1/user/room/messages " +
    """{"users": ["a", "b"]} """ +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic ejoxMjM0NTY3OA=="))
        .withJsonBody(Json.parse( """{"users": ["a", "b"]}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "PUT /api/v0.1/user/room/messages " +
    """{} """ +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
        .withJsonBody(Json.parse( """{}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/user/room/messages " +
    """{"users": ["a", "b"]} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
        .withJsonBody(Json.parse( """{"users": ["a", "b"]}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

}
