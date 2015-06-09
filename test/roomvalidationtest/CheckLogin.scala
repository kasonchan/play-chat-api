package roomvalidationtest

import play.api.test.PlaySpecification
import validations.RoomValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckLogin extends PlaySpecification with RoomValidation {

  "checkLogin(None) mustEqual " +
    "Right(\"No login is found\")" in {
    checkLogin(None) mustEqual Right("No login is found")
  }

  "checkLogin(Some(\"\")) mustEqual " +
    "Right(\"\")" in {
    checkLogin(Some("")) mustEqual Right("")
  }

  "checkLogin(Some(\"1\")) mustEqual Right(\"1\")" in {
    checkLogin(Some("1")) mustEqual Right("1")
  }

  "checkLogin(Some(\"12345678901234567890123456789012345678901234567890\")) mustEqual " +
    "Right(\"12345678901234567890123456789012345678901234567890\")" in {
    checkLogin(Some("12345678901234567890123456789012345678901234567890")) mustEqual
      Right("12345678901234567890123456789012345678901234567890")
  }

  "checkLogin(Some(\"123456789012345678901234567890123456789012345678901\")) mustEqual " +
    "Left(\"Login must be at most 50 characters\")" in {
    checkLogin(Some("123456789012345678901234567890123456789012345678901")) mustEqual
      Left("Login must be at most 50 characters")
  }

}
