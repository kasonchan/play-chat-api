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
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withJsonBody(Json.parse(
        """{ "users": [ "a", "b" ] } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Requires authentication"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b" ] } """.stripMargin +
    "must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Expecting text/json or application/json body"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b" ] } """.stripMargin +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic ejoxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "users": [ "a", "b" ] } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b" ] } """.stripMargin +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0"))
        .withJsonBody(Json.parse(
        """{ "users": [ "a", "b" ] } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b", "c" ] } """.stripMargin +
    "must be 404 Not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "users": [ "a", "b", "c" ] } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
    }
  }

  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b", "c", "d" ] } """.stripMargin +
    "must be 404 Not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "users": [ "a", "b", "c", "d" ] } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
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

  "POST /api/v0.1/user/room/messages " +
    """{ "users": [ "a", "b" ] } """.stripMargin +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/room/messages")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
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
