package test

import play.api.libs.json.Json
import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 5/28/15.
 */
class UserValidationTest extends PlaySpecification with UserValidation {

  /**
   * Check login
   */
  "checkLogin(None) mustEqual " +
    "Some(\"A username is required\")" in {
    checkLogin(None) mustEqual Some("A username is required")
  }

  "checkLogin(Some(\"\")) mustEqual " +
    "Some(\"Username must be at least 1 character and at most 50 characters\")" in {
    checkLogin(Some("")) mustEqual
      Some("Username must be at least 1 character and at most 50 characters")
  }

  "checkLogin(Some(\"1\")) mustEqual None " +
    "Some(\"Username must be at least 1 character and at most 50 characters\")" in {
    checkLogin(Some("1")) mustEqual None
  }

  "checkLogin(Some(\"12345678901234567890123456789012345678901234567890\")) mustEqual " +
    "None" in {
    checkLogin(Some("12345678901234567890123456789012345678901234567890")) mustEqual None
  }

  "checkLogin(Some(\"123456789012345678901234567890123456789012345678901\")) mustEqual " +
    "Some(\"Username must be at least 1 character and at most 50 characters\")" in {
    checkLogin(Some("123456789012345678901234567890123456789012345678901")) mustEqual
      Some("Username must be at least 1 character and at most 50 characters")
  }

  /**
   * Check email
   */
  "checkEmail(None) mustEqual Some(\"A email is required\")" in {
    checkEmail(None) mustEqual Some("A email is required")
  }

  "checkEmail(Some(\"a@a.com\")) mustEqual None" in {
    checkEmail(Some("a@a.com")) mustEqual None
  }

  "checkEmail(Some(\"\")) mustEqual " +
    "Some(\"Doesn't look like a valid email\")" in {
    checkEmail(Some("")) mustEqual Some("Doesn't look like a valid email")
  }

  "checkEmail(Some(\"aa.com\")) mustEqual " +
    "Some(\"Doesn't look like a valid email\")" in {
    checkEmail(Some("aa.com")) mustEqual Some("Doesn't look like a valid email")
  }

  "checkEmail(Some(\"a@acom\")) mustEqual " +
    "Some(\"Doesn't look like a valid email\")" in {
    checkEmail(Some("a@acom")) mustEqual Some("Doesn't look like a valid email")
  }

  "checkEmail(Some(\"aacom\")) mustEqual " +
    "Some(\"Doesn't look like a valid email\")" in {
    checkEmail(Some("aacom")) mustEqual Some("Doesn't look like a valid email")
  }

  "checkEmail(Some(\"playchatapi@playchatapi.org\")) mustEqual None" in {
    checkEmail(Some("playchatapi@playchatapi.org")) mustEqual None
  }

  "checkEmail(Some(\"playchatapiplaychatapi.org\")) mustEqual " +
    "Some(\"Doesn't look like a valid email\")" in {
    checkEmail(Some("playchatapiplaychatapi.org")) mustEqual Some("Doesn't look like a valid email")
  }

  "checkEmail(Some(\"playchatapi@playchatapiorg\")) mustEqual " +
    "Some(\"Doesn't look like a valid email\")" in {
    checkEmail(Some("playchatapi@playchatapiorg")) mustEqual Some("Doesn't look like a valid email")
  }

  "checkEmail(Some(\"playchatapiplaychatapiorg\")) mustEqual " +
    "Some(\"Doesn't look like a valid email\")" in {
    checkEmail(Some("playchatapiplaychatapiorg")) mustEqual Some("Doesn't look like a valid email")
  }

  /**
   * Check password
   */
  "checkPassword(None) mustEqual Some(\"A password is required\")" in {
    checkPassword(None) mustEqual Some("A password is required")
  }

  "checkPassword(Some(\"1234567\")) mustEqual " +
    "Some(\"Password must be at least 8 characters and at most 50 characters\")" in {
    checkPassword(Some("1234567")) mustEqual
      Some("Password must be at least 8 characters and at most 50 characters")
  }

  "checkPassword(Some(\"12345678\")) mustEqual None" in {
    checkPassword(Some("12345678")) mustEqual None
  }

  "checkPassword(Some(\"12345678901234567890123456789012345678901234567890\")) mustEqual None" in {
    checkPassword(Some("12345678901234567890123456789012345678901234567890")) mustEqual None
  }

  "checkPassword(Some(\"123456789012345678901234567890123456789012345678901\")) mustEqual " +
    "Some(\"Password must be at least 8 characters and at most 50 characters\")" in {
    checkPassword(Some("123456789012345678901234567890123456789012345678901")) mustEqual
      Some("Password must be at least 8 characters and at most 50 characters")
  }

  /**
   * Check type
   */
  "checkType(None) mustEqual Some(\"A type is needed\")" in {
    checkType(None) mustEqual Some("A type is needed")
  }

  "checkType(Some(\"admin\")) mustEqual Some(\"Bad credentials\")" in {
    checkType(Some("admin")) mustEqual Some("Bad credentials")
  }

  "checkType(Some(\"user\")) mustEqual None" in {
    checkType(Some("user")) mustEqual None
  }

  "checkType(Some(\"\")) mustEqual Some(\"Invalid type\")" in {
    checkType(Some("")) mustEqual Some("Invalid type")
  }

  /**
   * Check confirmed
   */
  "checkConfirmed(Some(true)) mustEqual Some(\"Default to false\")" in {
    checkConfirmed(Some(true)) mustEqual Some("Default to false")
  }

  "checkConfirmed(Some(false)) mustEqual None" in {
    checkConfirmed(Some(false)) mustEqual None
  }

  "checkConfirmed(None) mustEqual Some(\"Confirmed is needed\")" in {
    checkConfirmed(None) mustEqual Some("Confirmed is needed")
  }

  /**
   * Validate user
   */
  """validateUser({"login": "playchatapi",
             "avatar_url" : "",
             "type": "admin",
             "email": "playchatapi@playchatapi.com",
             "location": "playchatapi",
             "password": "P1aycha7<3i",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 }) mustEqual None""" in {
    val jsValue = Json.parse( """{"login": "playchatapi",
             "avatar_url" : "",
             "type": "admin",
             "email": "playchatapi@playchatapi.com",
             "location": "playchatapi",
             "password": "P1aycha7<3i",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """)
    validateUser(jsValue) mustEqual None
  }

  """validateUser({"login": "",
             "avatar_url" : "",
             "type": "admin",
             "email": "playchatapi@playchatapi.com",
             "location": "playchatapi",
             "password": "P1aycha7<3i",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 }) mustEqual """ +
    "Json.arr(\"Username must be at least 1 character and at most 50 characters\")" in {
    val jsValue = Json.parse( """{"login": "",
             "avatar_url" : "",
             "type": "admin",
             "email": "playchatapi@playchatapi.com",
             "location": "playchatapi",
             "password": "P1aycha7<3i",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """)
    validateUser(jsValue) mustEqual
      Some(Json.arr("Username must be at least 1 character and at most 50 characters"))
  }

  """validateUser({"login": "",
             "avatar_url" : "",
             "type": "admin",
             "email": "",
             "location": "playchatapi",
             "password": "",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 }) mustEqual """ +
    "Json.arr(\"Username must be at least 1 character and at most 50 characters\"," +
    "\"Doesn't look like a valid email\"," +
    "\"Password must be at least 8 characters and at most 50 characters\")" in {
    val jsValue = Json.parse( """{"login": "",
             "avatar_url" : "",
             "type": "admin",
             "email": "",
             "location": "playchatapi",
             "password": "",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """)
    validateUser(jsValue) mustEqual
      Some(Json.arr("Username must be at least 1 character and at most 50 characters",
        "Doesn't look like a valid email",
        "Password must be at least 8 characters and at most 50 characters"))
  }

  """validateUser({"login": "playchatapi",
             "avatar_url" : "",
             "type": "admin",
             "email": "",
             "location": "playchatapi",
             "password": "",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 }) mustEqual """ +
    "Json.arr(\"Doesn't look like a valid email\"," +
    "\"Password must be at least 8 characters and at most 50 characters\")" in {
    val jsValue = Json.parse( """{"login": "playchatapi",
             "avatar_url" : "",
             "type": "admin",
             "email": "",
             "location": "playchatapi",
             "password": "",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """)
    validateUser(jsValue) mustEqual
      Some(Json.arr("Doesn't look like a valid email",
        "Password must be at least 8 characters and at most 50 characters"))
  }

  """validateUser({"login": "playchatapi",
             "avatar_url" : "",
             "type": "admin",
             "email": "",
             "location": "playchatapi",
             "password": "12345678",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 }) mustEqual """ +
    "Json.arr(\"Doesn't look like a valid email\")" in {
    val jsValue = Json.parse( """{"login": "playchatapi",
             "avatar_url" : "",
             "type": "admin",
             "email": "",
             "location": "playchatapi",
             "password": "12345678",
             "confirmed": true,
             "created_at": 1432441527583,
             "updated_at": 1432441527583 } """)
    validateUser(jsValue) mustEqual
      Some(Json.arr("Doesn't look like a valid email"))
  }

}