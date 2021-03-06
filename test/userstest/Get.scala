package userstest

import json.JSON
import play.api.libs.json.Json
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/6/15.
 */
object Get extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "GET /api/v0.1/user a must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val json = Json.parse(contentAsString(response.get))
      (json \ "login").as[String] mustEqual "a"
      (json \ "avatar_url").as[String] mustEqual ""
      (json \ "type").as[String] mustEqual "user"
      (json \ "email").as[String] mustEqual "a@a.com"
      (json \ "location").as[String] mustEqual "a"
      (json \ "confirmed").as[Boolean] mustEqual true
      (json \ "created_at").as[Long] mustNotEqual (json \ "updated_at").as[Long]
      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/user b must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YjoxMjM0NTY3OA=="))
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val json = Json.parse(contentAsString(response.get))
      (json \ "login").as[String] mustEqual "b"
      (json \ "avatar_url").as[String] mustEqual ""
      (json \ "type").as[String] mustEqual "user"
      (json \ "email").as[String] mustEqual "b@b.com"
      (json \ "location").as[String] mustEqual ""
      (json \ "confirmed").as[Boolean] mustEqual true
      (json \ "created_at").as[Long] mustNotEqual (json \ "updated_at").as[Long]
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

  "GET /api/v0.1/user a must be 401 Unauthorized Bad credentials" in {
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

      val json = Json.parse(contentAsString(response.get))
      (json \ "login").as[String] mustEqual "playchat"
      (json \ "avatar_url").as[String] mustEqual ""
      (json \ "type").as[String] mustEqual "user"
      (json \ "email").as[String] mustEqual "playchat@playchat.com"
      (json \ "location").as[String] mustEqual ""
      (json \ "confirmed").as[Boolean] mustEqual true
      (json \ "created_at").as[Long] mustEqual (json \ "updated_at").as[Long]
      result.header.status mustEqual 200
    }
  }

  "GET /api/v0.1/users/a must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/api/v0.1/users/a")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val json = Json.parse(contentAsString(response.get))
      (json \ "login").as[String] mustEqual "a"
      (json \ "avatar_url").as[String] mustEqual ""
      (json \ "type").as[String] mustEqual "user"
      (json \ "email").as[String] mustEqual "a@a.com"
      (json \ "location").as[String] mustEqual "a"
      (json \ "confirmed").as[Boolean] mustEqual true
      (json \ "created_at").as[Long] mustNotEqual (json \ "updated_at").as[Long]
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

}
