package uservalidationtest

import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckPassword extends PlaySpecification with UserValidation {

  "checkPassword(None) mustEqual Some(\"A password is required\")" in {
    checkPassword(None) mustEqual Some("A password is required")
  }

  "checkPassword(Some(\"\")) mustEqual " +
    "Some(\"Password must be at least 8 characters and at most 50 characters\")" in {
    checkPassword(Some("")) mustEqual
      Some("Password must be at least 8 characters and at most 50 characters")
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

}
