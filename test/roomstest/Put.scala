package roomstest

import json.JSON
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._


/**
 * Created by ka-son on 6/9/15.
 */
object Put extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "PUT /api/v0.1/user/room " +
    "must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Expecting text/json or application/json body"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/user/room " +
    "must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Expecting text/json or application/json body"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/user/room " +
    """{"login": "abcd", "users": ["a", "b", "c", "d"], "privacy": "private"} """ +
    "must be 401 Unauthorized Requires authentication" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withJsonBody(Json.parse(
        """{"login": "abcd",
          | "users": ["a", "b", "c", "d"],
          | "privacy": "private"}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Requires authentication"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "PUT /api/v0.1/user/room " +
    """{"login": "abcd", "users": ["a", "b", "c", "d"], "privacy": "private"} """ +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withHeaders(("Authorization", "Basic "))
        .withJsonBody(Json.parse(
        """{"login": "abcd",
          | "users": ["a", "b", "c", "d"],
          | "privacy": "private"}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "PUT /api/v0.1/user/room " +
    """{"users": ["a", "b", "c", "d"]} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{"users": ["a", "b", "c", "d"]}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "PUT /api/v0.1/user/room " +
    """{"users": ["a", "b", "c", "d"], "privacy": "private"} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{"users": ["a", "b", "c", "d"],
          | "privacy": "private"}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "PUT /api/v0.1/user/room " +
    """{"login": "abcdtest", "users": ["a", "b", "c", "d"]} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{"login": "abcdtest",
          | "users": ["a", "b", "c", "d"]}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "PUT /api/v0.1/user/room " +
    """{"login": "abcd", "users": ["a", "b", "c", "d"], "privacy": "public"} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{"login": "abcd",
          | "users": ["a", "b", "c", "d"],
          | "privacy": "public"}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "PUT /api/v0.1/user/room " +
    """{"login": true, "users": ["a", "b", "c", "d"], "privacy": 1234} """ +
    "must be 200 Ok Invalid inputs Set back to default" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user/room")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{"login": true,
          | "users": ["a", "b", "c", "d"],
          | "privacy": 1234}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

}
