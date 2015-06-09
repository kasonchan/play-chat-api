package roomvalidationtest

import play.api.test.PlaySpecification
import validations.RoomValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckPrivacy extends PlaySpecification with RoomValidation {

  "checkPrivacy(Some(\"public\")) mustEqual Right(\"public\")" in {
    checkPrivacy(Some("public")) mustEqual Right("public")
  }

  "checkPrivacy(Some(\"private\")) mustEqual Right(\"private\")" in {
    checkPrivacy(Some("private")) mustEqual Right("private")
  }

  "checkPrivacy(None) mustEqual Right(\"No privacy is found\")" in {
    checkPrivacy(None) mustEqual Right("No privacy is found")
  }

  "checkPrivacy(Some(\"test\")) mustEqual Left(\"Invalid privacy\")" in {
    checkPrivacy(Some("test")) mustEqual Left("Invalid privacy")
  }

}
