package uservalidationtest

import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckConfirmed extends PlaySpecification with UserValidation {

  "checkConfirmed(Some(true)) mustEqual Some(\"Default to false\")" in {
    checkConfirmed(Some(true)) mustEqual None
  }

  "checkConfirmed(Some(false)) mustEqual None" in {
    checkConfirmed(Some(false)) mustEqual None
  }

  "checkConfirmed(None) mustEqual Some(\"Invalid confirmed\")" in {
    checkConfirmed(None) mustEqual Some("Invalid confirmed")
  }

}
