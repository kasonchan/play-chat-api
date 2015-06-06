package uservalidationtest

import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckType extends PlaySpecification with UserValidation {

  "checkType(None) mustEqual Some(\"A type is needed\")" in {
    checkType(None) mustEqual Some("A type is needed")
  }

  "checkType(Some(\"admin\")) mustEqual Some(\"Bad credentials\")" in {
    checkType(Some("admin")) mustEqual Some("Bad credentials")
  }

  "checkType(Some(\"user\")) mustEqual None" in {
    checkType(Some("user")) mustEqual None
  }

  "checkType(Some(\"\")) mustEqual Some(\"Invalid type\")" in {
    checkType(Some("")) mustEqual Some("Invalid type")
  }

}
