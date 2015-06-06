package uservalidationtest

import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckConfirmed extends PlaySpecification with UserValidation {

  "checkConfirmed(Some(true)) mustEqual Some(\"Default to false\")" in {
    checkConfirmed(Some(true)) mustEqual Some("Default to false")
  }

  "checkConfirmed(Some(false)) mustEqual None" in {
    checkConfirmed(Some(false)) mustEqual None
  }

  "checkConfirmed(None) mustEqual Some(\"Confirmed is needed\")" in {
    checkConfirmed(None) mustEqual Some("Confirmed is needed")
  }

}
