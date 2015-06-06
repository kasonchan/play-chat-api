package test

import json.JSON
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 5/26/15.
 */
class UsersTest extends PlaySpecification with JSON {
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

  "GET /api/v0.1/user a must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/user a must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YToxMjM0"))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "GET /api/v0.1/user a must be 401 Unauthorized Requires authentication" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Requires authentication"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "GET /api/v0.1/user must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user")
        .withHeaders(("Authorization", ""))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse =
        Json.obj("messages" -> Json.arr("Bad credentials"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 401
    }
  }

  "GET /api/v0.1/users must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/playchat must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/playchat")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/a must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/a")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/nobody must be 404 Not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/nobody")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "z",
       "email": "z@z.com",
       "password": "12345678"} """ +
    "must be 201 Created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "z",
                                    | "email": "z@z.com",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 201
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "a",
       "email": "a@a.com",
       "password": "12345678"} """ +
    "must be 400 Bad request Login email are registered" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "a",
                                    | "email": "a@a.com",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Login is already registered",
          "Email is already registered"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "a",
       "email": "a@a.com" } """ +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "a",
                                    | "email": "a@a.com" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"email": "a@a.com",
       "password": "12345678" } """ +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "email": "a@a.com",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{} """ +
    "must be 400 Bad request Invalid Json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    "must be 400 Bad request Expecting json" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
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

  "POST /api/v0.1/users " +
    """{"login": "",
       "email": "a@a.com",
       "password": "12345678" } """ +
    "must be 400 Bad request Invalid login" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "",
                                    | "email": "a@a.com",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Username must be at least 1 character and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "",
       "email": "",
       "password": "12345678" } """ +
    "must be 400 Bad request Invalid login email" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "",
                                    | "email": "",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Username must be at least 1 character and at most 50 characters",
            "Doesn't look like a valid email"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "",
       "email": "a@a.com",
       "password": "" } """ +
    "must be 400 Bad request Invalid Login password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "",
                                    | "email": "a@a.com",
                                    | "password": "" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Username must be at least 1 character and at most 50 characters",
            "Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "",
       "email": "",
       "password": "" } """ +
    "must be 400 Bad request Invalid Login email password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "",
                                    | "email": "",
                                    | "password": "" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Username must be at least 1 character and at most 50 characters",
            "Doesn't look like a valid email",
            "Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "a",
       "email": "a",
       "password": "12345678" } """ +
    "must be 400 Bad request Invalid email" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "a",
                                    | "email": "a",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Doesn't look like a valid email"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "a",
       "email": "a",
       "password": "1234567" } """ +
    "must be 400 Bad request Invalid email password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "a",
                                    | "email": "a",
                                    | "password": "1234567" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Doesn't look like a valid email",
            "Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "a",
       "email": "a@a.com",
       "password": "" } """ +
    "must be 400 Bad request Invalid password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "a",
                                    | "email": "a@a.com",
                                    | "password": "" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "123456789012345678901234567890123456789012345678901",
       "email": "a@a.com",
       "password": "12345678" } """ +
    "must be 400 Bad request Invalid login" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "",
                                    | "email": "a@a.com",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Username must be at least 1 character and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "",
       "email": "a@a.com",
       "password": "1234567" } """ +
    "must be 400 Bad request Invalid login password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "",
                                    | "email": "a@a.com",
                                    | "password": "1234567" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Username must be at least 1 character and at most 50 characters",
            "Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "",
       "email": "a@a.com",
       "password": "123456789012345678901234567890123456789012345678901" } """ +
    "must be 400 Bad request Invalid login password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "",
                                    | "email": "a@a.com",
                                    | "password": "123456789012345678901234567890123456789012345678901" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Username must be at least 1 character and at most 50 characters",
            "Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "a",
       "email": "a@a.com",
       "password": "1234567" } """ +
    "must be 400 Bad request Invalid password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "a",
                                    | "email": "a@a.com",
                                    | "password": "1234567" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users " +
    """{"login": "a",
       "email": "a@a.com",
       "password": "123456789012345678901234567890123456789012345678901" } """ +
    "must be 400 Bad request Invalid password" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "a",
                                    | "email": "a@a.com",
                                    | "password": "123456789012345678901234567890123456789012345678901" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" ->
          Json.arr("Password must be at least 8 characters and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/user " +
    """{"location": ""} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withJsonBody(Json.parse( """{ "location": "" }""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

  "PUT /api/v0.1/user " +
    """{ "location": 12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901 } """ +
    "must be 400 Bad request Empty location" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withJsonBody(Json.parse( """{ "location": 12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901 }""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Location must be at most 100 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/user " +
    """{} """ +
    "must be 400 Bad request Empty location" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withJsonBody(Json.parse( """{}""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Location is not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/user must be 400 Bad request Empty location" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
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

  "PUT /api/v0.1/user must be 401 Bad request Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withHeaders(("Authorization", ""))
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

  "PUT /api/v0.1/user must be 401 Bad request Requires authentication" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
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

  "PUT /api/v0.1/user " +
    """{"location": "a"} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withJsonBody(Json.parse( """{ "location": "a" }""".stripMargin))
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

}
