package validations

import play.api.libs.json.{JsValue, Json}

/**
 * Created by ka-son on 5/28/15.
 */
trait UserValidation {

  /**
   * Check login
   * Valid length range: 1 - 50
   * Return None if login is valid
   * Otherwise return Some error message
   * @param login Option[String]
   * @return Option[String]
   */
  def checkLogin(login: Option[String]): Option[String] = {
    login match {
      case Some(l) =>
        if ((l.length < 1) || (l.length > 50))
          Some("Username must be at least 1 character and at most 50 characters")
        else
          None
      case None => Some("A username is required")
    }
  }

  /**
   * Check email
   * Valid format: ([a-zA-Z0-9]+)@([a-zA-Z0-9]+)(\.)([a-zA-Z0-9]+)
   * Return None if the email is valid
   * Otherwise return Some error message
   * @param email Option[String]
   * @return Option[String]
   */
  def checkEmail(email: Option[String]): Option[String] = {
    email match {
      case Some(e) =>
        val emailPattern = """([a-zA-Z0-9]+)@([a-zA-Z0-9]+)(\.)([a-zA-Z0-9]+)"""

        if (!e.matches(emailPattern))
          Some("Doesn't look like a valid email")
        else
          None
      case None => Some("A email is required")
    }
  }

  /**
   * Check password
   * Valid length range: 8 - 50
   * Return None if the password is valid
   * Otherwise return Some with error message
   * @param password Option[String]
   * @return Option[String]
   */
  def checkPassword(password: Option[String]): Option[String] = {
    password match {
      case Some(p) =>
        if ((p.length < 8) || (p.length > 50))
          Some("Password must be at least 8 characters and at most 50 characters")
        else
          None
      case None => Some("A password is required")
    }
  }

  /**
   * Validate user
   * Call checkLogin, checkEmail and checkPassword functions to check the user's
   * information
   * Returns None if the user is valid
   * Otherwise return Some error message
   * @param jsValue JsValue
   * @return Option[JsValue]
   */
  def validateUser(jsValue: JsValue): Option[JsValue] = {
    val login: Option[String] = (jsValue \ "login").asOpt[String]
    val email: Option[String] = (jsValue \ "email").asOpt[String]
    val password: Option[String] = (jsValue \ "password").asOpt[String]

    val lv: Option[String] = checkLogin(login)
    val ev: Option[String] = checkEmail(email)
    val pv: Option[String] = checkPassword(password)

    val resultSeq: Seq[Option[String]] = Seq(lv, ev, pv)

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
