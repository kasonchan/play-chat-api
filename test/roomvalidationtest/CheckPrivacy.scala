package roomvalidationtest

import play.api.test.PlaySpecification
import validations.RoomValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckPrivacy extends PlaySpecification with RoomValidation {

  "checkPrivacy(Some(\"public\")) mustEqual None" in {
    checkPrivacy(Some("public")) mustEqual None
  }

  "checkPrivacy(Some(\"private\")) mustEqual None" in {
    checkPrivacy(Some("private")) mustEqual None
  }

  "checkPrivacy(None) mustEqual Some(\"Privacy is required\")" in {
    checkPrivacy(None) mustEqual Some("Privacy is required")
  }

  "checkPrivacy(Some(\"test\")) mustEqual Some(\"Invalid privacy\")" in {
    checkPrivacy(Some("test")) mustEqual Some("Invalid privacy")
  }

}
