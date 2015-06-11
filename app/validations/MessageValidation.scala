package validations

import play.api.libs.json.{Json, JsArray, JsValue}

/**
 * Created by ka-son on 6/10/15.
 */
trait MessageValidation extends UserValidation {

  /**
   * Check users
   * Limit to 100 users
   * Return None if the users are valid
   * Otherwise return Some error messages
   * @param users Option[JsArray]
   * @return Option[String]
   */
  def checkUsers(users: Option[JsValue]): Option[String] = {
    users match {
      case Some(users) =>
        val us = users.as[Seq[String]]
        if ((us.toSet.size < 2) || (us.toSet.size > 100))
          Some("Number of users must be at least 2 and at most 100")
        else
          None
      case None => Some("Users are required")
      case _ => Some("Invalid users")
    }
  }

  /**
   * Check text
   * Valid length range: 1 - 1000
   * Return None if the text is valid
   * Otherwise return Some error messages
   * @param text
   * @return
   */
  def checkText(text: Option[String]): Option[String] = {
    text match {
      case Some(t) =>
        if ((t.length < 1) || (t.length > 1000))
          Some("Text must be at least 1 character and at most 1000 characters")
        else
          None
      case None => Some("Text is required")
      case _ => Some("Invalid text")
    }
  }

  def validateMessage(msg: JsValue): Option[JsValue] = {
    val owner = (msg \ "owner").asOpt[String]
    val users = (msg \ "users").asOpt[JsArray]
    val text = (msg \ "text").asOpt[String]

    val ov = checkLogin(owner)
    val uv = checkUsers(users)
    val tv = checkText(text)

    val resultSeq: Seq[Option[String]] = Seq(ov, uv, tv)

    val resultSome: Seq[Option[String]] = resultSeq.filter(r => r != None)

    resultSome match {
      case Seq() => None
      case rs: Seq[Option[String]] =>
        val result: Seq[String] = resultSome.map(x => x.getOrElse(""))
        val resultJs: JsValue = Json.toJson(result)
        Some(resultJs)
    }
  }

}
