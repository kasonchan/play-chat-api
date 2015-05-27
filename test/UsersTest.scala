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

  "GET /api/v0.1 must be 200" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue = Json.obj("user_url" -> "/api/v0.1/users/{user}")

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/user must be None" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users")
      val response = route(request)
      response must beNone
    }
  }

  "GET /api/v0.1/users must be None" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users")
      val response = route(request)
      response must beNone
    }
  }

  "GET /api/v0.1/users/playchat must be 200" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/playchat")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/nobody must be 404" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/nobody")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse = Json.obj("messages" -> Json.arr("Not found :("))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
    }
  }

  "POST /api/v0.1/users/register " +
    """{"login": "playchat",
             "avatar_url" : "",
             "type": "admin",
             "email": "playchat@playchat.com",
             "location": "playchat",
             "password": "P1aycha7<3i",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """ +
    "must be 400 " in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users/register")
        .withHeaders("Content-Type" -> "application/json; charset=utf-8")
        .withJsonBody(Json.parse( """{ "login": "playchat",
                                    | "avatar_url": "",
                                    | "type": "admin",
                                    | "email": "playchat@playchat.com",
                                    | "location": "playchat",
                                    | "password": "P1aycha7<3i",
                                    | "confirmed": true,
                                    | "created_at": 1432441527583,
                                    | "updated_at": 1432441527583 }""".stripMargin))
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

  "POST /api/v0.1/users/register " +
    """{"login": "play",
             "avatar_url" : "",
             "type": "admin",
             "email": "playchat@playchat.com",
             "location": "playchat",
             "password": "P1aycha7<3i",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """ +
    "must be 400" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users/register")
        .withHeaders("Content-Type" -> "application/json; charset=utf-8")
        .withJsonBody(Json.parse( """ { "login": "play",
                                    | "avatar_url": "",
                                    | "type": "admin",
                                    | "email": "playchat@playchat.com",
                                    | "location": "playchat",
                                    | "password": "P1aycha7<3i",
                                    | "confirmed": true,
                                    | "created_at": 1432441527583,
                                    | "updated_at": 1432441527583 } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Email is already registered"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users/register " +
    """{"login": "playchat",
             "avatar_url" : "",
             "type": "admin",
             "email": "playchat@playchat.co",
             "location": "playchat",
             "password": "P1aycha7<3i",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """ +
    "must be 400" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users/register")
        .withHeaders("Content-Type" -> "application/json; charset=utf-8")
        .withJsonBody(Json.parse( """ { "login": "playchat",
                                    | "avatar_url": "",
                                    | "type": "admin",
                                    | "email": "playchat@playchat.co",
                                    | "location": "playchat",
                                    | "password": "P1aycha7<3i",
                                    | "confirmed": true,
                                    | "created_at": 1432441527583,
                                    | "updated_at": 1432441527583 } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Login is already registered"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /api/v0.1/users/register " +
    """{"login": "a",
             "avatar_url" : "",
             "type": "user",
             "email": "a@a.com",
             "location": "a",
             "password": "12345678",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """ +
    "must be 201" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/api/v0.1/users/register")
        .withHeaders("Content-Type" -> "application/json; charset=utf-8")
        .withJsonBody(Json.parse( """ { "login": "a",
                                    | "avatar_url": "",
                                    | "type": "user",
                                    | "email": "a@a.com",
                                    | "location": "a",
                                    | "password": "12345678",
                                    | "confirmed": true,
                                    | "created_at": 1432441527583,
                                    | "updated_at": 1432441527583 } """.stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("login" -> "a",
          "avatar_url" -> "",
          "type" -> "user",
          "email" -> "a@a.com",
          "location" -> "a",
          "confirmed" -> true,
          "created_at" -> 1432441527583L,
          "updated_at" -> 1432441527583L)

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 201
    }
  }

}
