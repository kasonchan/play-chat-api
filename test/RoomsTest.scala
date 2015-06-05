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

  "GET /api/v0.1/rooms must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/playchat/rooms must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/playchat/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/a/rooms must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/a/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/b/rooms must be 404 Not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/b/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
    }
  }

  "GET /api/v0.1/users/nobody/rooms must be 404 Not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/nobody/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
    }
  }

  "GET /api/v0.1/users/b/rooms must be 404 Not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/b/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
    }
  }

  "GET /api/v0.1/user/rooms a must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user/rooms")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/user/rooms playchat must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user/rooms")
        .withHeaders(("Authorization", "Basic cGxheWNoYXQ6UDFheWNoYTc8M2k="))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/user/rooms b must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user/rooms")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/user/rooms a must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user/rooms")
        .withHeaders(("Authorization", ""))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "GET /api/v0.1/user/rooms a must be 401 Unauthorized Requires authenticaion" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user/rooms")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Requires authentication"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "c", "d" ] } """ +
    "must be 201 Created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "c", "d" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic YzoxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 201
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "a", "b" ] } """ +
    "must be 400 Bad request Rooms is created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "a", "b" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Room is already created"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/rooms " +
    "must be 400 Bad request Expecting Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
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

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "a", "b" ] } """ +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "a", "b" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic "))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "a", "b" ] } """ +
    "must be 401 Unauthorized Requires authentication" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "a", "b" ] }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Requires authentication"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{} """ +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{}""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "a", "z" ] } """ +
    "must be 400 Bad request Invalid users" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "a", "z" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Invalid users"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "z", "a" ] } """ +
    "must be 400 Bad request Invalid users" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "z", "a" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic ejoxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Invalid users"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "z", "a" ] } """ +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "z", "a" ] }""".stripMargin))
        .withHeaders(("Authorization", ""))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "b", "z", "a" ] } """ +
    "must be 400 Bad request Invalid users" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "b", "z", "a" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic ejoxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Invalid users"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "a", "a" ] } """ +
    "must be 400 Bad request Invalid number of users" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "a", "a" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Number of users must be at least 2 and at most 100"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/user/rooms " +
    """{ "users": [ "a", "a", "b" ] } """ +
    "must be 400 Bad request Users must not be duplicated" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/user/rooms")
        .withJsonBody(Json.parse( """{ "users": [ "a", "a", "b" ] }""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" ->
        Json.arr("Users must not be duplicated"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

}
