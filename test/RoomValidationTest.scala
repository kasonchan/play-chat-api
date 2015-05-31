package test

import play.api.test.PlaySpecification
import validations.RoomValidation

/**
 * Created by ka-son on 5/31/15.
 */
class RoomValidationTest extends PlaySpecification with RoomValidation {

  /**
   * Check login
   */
  "checkLogin(None) mustEqual " +
    "Some(\"A login is required\")" in {
    checkLogin(None) mustEqual Some("A login is required")
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
   * Check users
   */
  "checkUsers(Some(Seq(\"a\")) mustEqual Some(\"Users\' length must be at least 2 and at most 100\")" in {
    checkUsers(Some(Seq("a"))) mustEqual Some("Users' length must be at least 2 and at most 100")
  }

  "checkUsers(Some(Seq(\"a\", \"b\")) mustEqual Some(Seq(\"a\", \"b\")" in {
    checkUsers(Some(Seq("a", "b"))) mustEqual Some(Seq("a", "b"))
  }

  "checkUsers(Some(Seq(\"a\", \"b\", \"d\", \"e\", \"c\"))) mustEqual Some(Seq(\"a\", \"b\", \"d\", \"e\", \"c\"))" in {
    checkUsers(Some(Seq("a", "b", "d", "e", "c"))) mustEqual Some(Seq("a", "b", "c", "d", "e"))
  }

  "checkUsers(Some(Seq(\"z\", \"b\", \"a\", \"b\", \"c\"))) mustEqual Some(Seq(\"a\", \"b\", \"b\", \"c\", \"z\"))" in {
    checkUsers(Some(Seq("z", "b", "a", "b", "c"))) mustEqual Some(Seq("a", "b", "b", "c", "z"))
  }

  /**
   * Check privacy
   */
  "checkPrivacy(Some(\"public\")) mustEqual None" in {
    checkPrivacy(Some("public")) mustEqual None
  }

  "checkPrivacy(Some(\"private\")) mustEqual None" in {
    checkPrivacy(Some("private")) mustEqual None
  }

  "checkPrivacy(None) mustEqual Some(\"Privacy is required\")" in {
    checkPrivacy(None) mustEqual Some("Privacy is required")
  }

}
