package messagevalidationtest

import play.api.test.PlaySpecification
import validations.MessageValidation

/**
 * Created by ka-son on 6/10/15.
 */
object CheckText extends PlaySpecification with MessageValidation {

  "checkText(None) mustEqual " +
    "Some(\"Text is required\")" in {
    checkText(None) mustEqual Some("Text is required")
  }

  "checkText(Some(\"\")) mustEqual " +
    "Some(\"Text must be at least 1 character and at most 1000 characters\")" in {
    checkText(Some("")) mustEqual Some("Text must be at least 1 character and at most 1000 characters")
  }

  "checkText(Some(\"1\")) mustEqual " +
    "None" in {
    checkText(Some("1")) mustEqual None
  }

  "checkText(Some(\"1234567890123456789012345678901234567890123456789012345678901234567890\")) mustEqual " +
    "None" in {
    checkText(Some("1234567890123456789012345678901234567890123456789012345678901234567890")) mustEqual None
  }

  val text1000 = (for (x <- 1 to 1000) yield "X").mkString

  "checkText(Some(" + text1000 + ")) mustEqual " +
    "None" in {
    checkText(Some(text1000)) mustEqual None
  }

  val text1001 = (for (x <- 1 to 1001) yield "X").mkString

  "checkText(Some(" + text1001 + ")) mustEqual " +
    "Some(\"Text must be at least 1 character and at most 1000 characters\")" in {
    checkText(Some(text1001)) mustEqual
      Some("Text must be at least 1 character and at most 1000 characters")
  }

}
