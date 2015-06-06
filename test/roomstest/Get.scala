package roomstest

import json.JSON
import play.api.libs.json.Json
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/6/15.
 */
object Get extends PlaySpecification with JSON {

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

}
