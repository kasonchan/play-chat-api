package userstest

import json.JSON
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 6/6/15.
 */
object Put extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  /**
   * Tests with location
   */
  "PUT /api/v0.1/user " +
    """{"location": ""} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse( """{ "location": "" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val location = (Json.parse(contentAsString(response.get)) \ "location").as[String]

      location mustEqual "a"
      result.header.status mustEqual 200
    }
  }

  "PUT /api/v0.1/user " +
    """{ "location": "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901" } """ +
    "must be 400 Bad request Location must be at most 100 characters" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse( """{ "location": "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901" }""".stripMargin))
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
    """{}""" +
    "must be 400 Bad request Location is not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse( """{}""".stripMargin))
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

  "PUT /api/v0.1/user " +
    "must be 400 Bad request Expecting text/json or application/json body" in {
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

  "PUT /api/v0.1/user must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withHeaders(("Authorization", "BASE "))
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

  "PUT /api/v0.1/user must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
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

  "PUT /api/v0.1/user " +
    """{"location": "a"} """ +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withJsonBody(Json.parse( """{ "location": "a" }""".stripMargin))
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
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic "))
        .withJsonBody(Json.parse( """{ "location": "a" }""".stripMargin))
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

  "PUT /api/v0.1/user " +
    """{"location": "a"} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/user")
        .withHeaders(("Authorization", "Basic YToxMjM0NTY3OA=="))
        .withJsonBody(Json.parse( """{ "location": "a" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val location = (Json.parse(contentAsString(response.get)) \ "location").as[String]

      location mustEqual ""
      result.header.status mustEqual 200
    }
  }

  /**
   * Tests with confirmed
   */
  "PUT /api/v0.1/users/b " +
    """{"confirmed": "Invalid"} """ +
    "must be 400 Bad request Confirmed is not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/b")
        .withHeaders(("Authorization", "Basic cGxheWNoYXRhZG1pbjpQMWF5Y2hhN2FkbTFuPDNp"))
        .withJsonBody(Json.parse( """{ "confirmed": "Invalid" }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Invalid confirmed"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "PUT /api/v0.1/users/z " +
    """{"confirmed": true} """ +
    "must be 404 Not found" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/z")
        .withHeaders(("Authorization", "Basic cGxheWNoYXRhZG1pbjpQMWF5Y2hhN2FkbTFuPDNp"))
        .withJsonBody(Json.parse( """{ "confirmed": true }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("messages" -> Json.arr("Not found"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 404
    }
  }

  "PUT /api/v0.1/users/b " +
    """{"confirmed": true } """ +
    "must be 401 Unauthorized Bad credentials" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/b")
        .withHeaders(("Authorization", "Basic cGxheWNoYXRhZG1p"))
        .withJsonBody(Json.parse( """{ "confirmed": true }""".stripMargin))
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

  "PUT /api/v0.1/users/b " +
    """{"confirmed": true } """ +
    "must be 401 Unauthorized Requires authentication " in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/b")
        .withJsonBody(Json.parse( """{ "confirmed": true }""".stripMargin))
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

  "PUT /api/v0.1/users/b " +
    "must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/b")
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

  "PUT /api/v0.1/users/b " +
    "must be 400 Bad request Expecting text/json or application/json body" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/b")
        .withHeaders(("Authorization", "Basic cGxheWNoYXRhZG1pbjpQMWF5Y2hhN2FkbTFuPDNp"))
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

  "PUT /api/v0.1/users/b " +
    """{"confirmed": false} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/b")
        .withHeaders(("Authorization", "Basic cGxheWNoYXRhZG1pbjpQMWF5Y2hhN2FkbTFuPDNp"))
        .withJsonBody(Json.parse( """{ "confirmed": false }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val confirmed = (Json.parse(contentAsString(response.get)) \ "confirmed").as[Boolean]

      confirmed mustEqual true
      result.header.status mustEqual 200
    }
  }

  "PUT /api/v0.1/users/b " +
    """{"confirmed": true} """ +
    "must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(PUT, "/api/v0.1/users/b")
        .withHeaders(("Authorization", "Basic cGxheWNoYXRhZG1pbjpQMWF5Y2hhN2FkbTFuPDNp"))
        .withJsonBody(Json.parse( """{ "confirmed": true }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val confirmed = (Json.parse(contentAsString(response.get)) \ "confirmed").as[Boolean]

      confirmed mustEqual false
      result.header.status mustEqual 200
    }
  }

}
