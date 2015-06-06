package uservalidationtest

import play.api.libs.json.Json
import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object ValidateUser extends PlaySpecification with UserValidation {

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
