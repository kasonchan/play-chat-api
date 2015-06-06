package uservalidationtest

import play.api.test.PlaySpecification
import validations.UserValidation

/**
 * Created by ka-son on 6/6/15.
 */
object CheckLocation extends PlaySpecification with UserValidation {

  "checkLocation(None) mustEqual Left(\"Location is not found\")" in {
    checkLocation(None) mustEqual Left("Location is not found")
  }

  "checkLocation(Some(\"\") mustEqual Right(\"\")" in {
    checkLocation(Some("")) mustEqual Right("")
  }

  "checkLocation(Some(\"1\") mustEqual Right(\"1\")" in {
    checkLocation(Some("1")) mustEqual Right("1")
  }

  "checkLocation(Some(\"12345678901234567890123456789012345678901234567890\") " +
    "mustEqual Right(\"12345678901234567890123456789012345678901234567890\")" in {
    checkLocation(Some("12345678901234567890123456789012345678901234567890")) mustEqual
      Right("12345678901234567890123456789012345678901234567890")
  }

  "checkLocation(Some(\"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\") " +
    "mustEqual Right(\"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\")" in {
    checkLocation(Some("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")) mustEqual
      Right("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
  }

  "checkLocation(Some(\"12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901\") " +
    "mustEqual Left(\"Location must be at most 100 characters\")" in {
    checkLocation(Some("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")) mustEqual
      Left("Location must be at most 100 characters")
  }

}
