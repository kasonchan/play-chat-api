package roomvalidationtest

import play.api.test.PlaySpecification
import validations.RoomValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckUsers extends PlaySpecification with RoomValidation {

  "checkUsers(Some(Seq()) mustEqual Some(\"Users\' length must be at least 2 and at most 100\")" in {
    checkUsers(Some(Seq())) mustEqual Some("Users' length must be at least 2 and at most 100")
  }

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

}
