package messagestest

import json.JSON
import play.api.libs.json.Json
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/10/15.
 */
object Post extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "POST /api/v0.1/user/messages " +
    "must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
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

  "POST /api/v0.1/user/messages " +
    """{ "owner": "a",
      | "users": [ "a", "b" ],
      | "text": "Hi, this is A." } """.stripMargin +
    "must be 401 Unauthorized Requires authentication" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withJsonBody(Json.parse(
        """{ "owner": "a",
          | "users": [ "a", "b" ],
          | "text": "Hi, this is A. 1st message" }""".stripMargin))
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

  "POST /api/v0.1/user/messages " +
    """{ "owner": "a",
      | "users": [ "a", "b" ],
      | "text": "Hi, this is A." } """.stripMargin +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NT"))
        .withJsonBody(Json.parse(
        """{ "owner": "a",
          | "users": [ "a", "b" ],
          | "text": "Hi, this is A. 2nd message." }""".stripMargin))
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

  "POST /api/v0.1/user/messages " +
    """{ "owner": "a",
      | "text": "Hi, this is A. 2nd message." } """.stripMargin +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "owner": "a",
          | "text": "Hi, this is A. 2nd message." }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/messages " +
    """{ "text": "Hi, this is A. 2nd message." } """.stripMargin +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "text": "Hi, this is A. 2nd message." }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/messages " +
    """{  } """.stripMargin +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{  }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/messages " +
    """{ "owner": "a",
      | "users": [ "a", "b" ] } """.stripMargin +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "owner": "a",
          | "users": [ "a", "b" ] }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/messages " +
    """{ "owner": "b",
      | "users": [ "a", "b" ],
      | "text": "Hi, this is B." } """.stripMargin +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "owner": "b",
          | "users": [ "a", "b" ],
          | "text": "Hi, this is B." }""".stripMargin))
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

  "POST /api/v0.1/user/messages " +
    """{ "owner": "b",
      | "users": [ "z", "b" ],
      | "text": "Hi, this is B." } """.stripMargin +
    "must be 400 Bad request Invalid users" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "owner": "b",
          | "users": [ "z", "b" ],
          | "text": "Hi, this is B." }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Invalid users"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/messages " +
    """{ "owner": "b",
      | "users": [ "z", "b" ],
      | "text": "Hi, this is B." } """.stripMargin +
    "must be 400 Bad request Invalid users" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "owner": "b",
          | "users": [ "z", "b" ],
          | "text": "Hi, this is B." }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Invalid users"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/messages " +
    """{ "owner": "b",
      | "users": [ "a", "b" ],
      | "text": "Hi, this is B." } """.stripMargin +
    "must be 201 Created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "owner": "b",
          | "users": [ "a", "b" ],
          | "text": "Hi, this is B." }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 201
    }
  }

  "POST /api/v0.1/user/messages " +
    """{ "owner": "a",
      | "users": [ "a", "b" ],
      | "text": "Hi, this is A." } """.stripMargin +
    "must be 201 Created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/messages")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse(
        """{ "owner": "a",
          | "users": [ "a", "b" ],
          | "text": "Hi, this is A." }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 201
    }
  }

}
