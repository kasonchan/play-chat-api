package messagevalidationtest

import play.api.libs.json.Json
import play.api.test.PlaySpecification
import validations.MessageValidation

/**
 * Created by ka-son on 6/10/15.
 */
object CheckUsers extends PlaySpecification with MessageValidation {

  "checkUsers(None) mustEqual " +
    "Some(\"Users are required\")" in {
    checkUsers(None) mustEqual Some("Users are required")
  }

  "checkUsers(Some(\"a\") mustEqual " +
    "Some(\"Number of users must be at least 2 and at most 100\")" in {
    checkUsers(Some(Json.arr("a"))) mustEqual Some("Number of users must be at least 2 and at most 100")
  }

  "checkUsers(Some(\"a\", \"a\") mustEqual " +
    "Some(\"Number of users must be at least 2 and at most 100\")" in {
    checkUsers(Some(Json.arr("a", "a"))) mustEqual Some("Number of users must be at least 2 and at most 100")
  }

  "checkUsers(Some(Json.arr(\"a\", \"b\"))) mustEqual " +
    None in {
    checkUsers(Some(Json.arr("a", "b"))) mustEqual None
  }

  "checkUsers(Some(Json.arr())) mustEqual " +
    "Some(\"Number of users must be at least 2 and at most 100\")" in {
    checkUsers(Some(Json.arr())) mustEqual
      Some("Number of users must be at least 2 and at most 100")
  }

  val j100 = Json.toJson(for (x <- 1 to 100) yield x.toString)

  "checkUsers(Some(" + j100 + "))) mustEqual " +
    None in {
    checkUsers(Some(j100)) mustEqual None
  }

  val j101 = Json.toJson(for (x <- 1 to 101) yield x.toString)

  "checkUsers(Some(" + j101 + "))) mustEqual " +
    "Some(\"Number of users must be at least 2 and at most 100\")" in {
    checkUsers(Some(j101)) mustEqual Some("Number of users must be at least 2 and at most 100")
  }

}
