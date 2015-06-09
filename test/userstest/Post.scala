package userstest

import json.JSON
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/6/15.
 */
object Post extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "POST /api/v0.1/users " +
    """{"login": "y",
       "email": "y@y.com",
       "password": "12345678"} """ +
    "must be 201 Created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users")
        .withJsonBody(Json.parse( """{ "login": "y",
                                    | "email": "y@y.com",
                                    | "password": "12345678" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val json = Json.parse(contentAsString(response.get))
      (json \ "login").as[String] mustEqual "y"
      (json \ "avatar_url").as[String] mustEqual ""
      (json \ "type").as[String] mustEqual "user"
      (json \ "email").as[String] mustEqual "y@y.com"
      (json \ "location").as[String] mustEqual ""
      (json \ "confirmed").as[Boolean] mustEqual false
      (json \ "created_at").as[Long] mustEqual (json \ "updated_at").as[Long]
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

}
