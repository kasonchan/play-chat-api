package uservalidationtest

import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckLogin extends PlaySpecification with UserValidation {

  "checkLogin(None) mustEqual " +
    "Some(\"A username is required\")" in {
    checkLogin(None) mustEqual Some("A username is required")
  }

  "checkLogin(Some(\"\")) mustEqual " +
    "Some(\"Username must be at least 1 character and at most 50 characters\")" in {
    checkLogin(Some("")) mustEqual
      Some("Username must be at least 1 character and at most 50 characters")
  }

  "checkLogin(Some(\"1\")) mustEqual None " in {
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

}
