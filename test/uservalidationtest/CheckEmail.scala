package uservalidationtest

import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckEmail extends PlaySpecification with UserValidation {

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

}
