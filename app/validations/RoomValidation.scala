package validations

import play.api.libs.json.JsValue

/**
 * Created by ka-son on 5/31/15.
 */
trait RoomValidation {

  /**
   * Check login
   * Valid length range: 0 - 50
   * Return None if login is valid
   * Otherwise return Some error message
   * @param login Option[String]
   * @return Option[String]
   */
  def checkLogin(login: Option[String]): Either[String, String] = {
    login match {
      case Some(l: String) =>
        if (l.length > 50) Left("Login must be at most 50 characters")
        else Right(l)
      case None => Right("No login is found")
      case _ => Left("Invalid login")
    }
  }

  /**
   * Check users
   * Length of the users must be at least 2 and at most 100
   * Return sorted Some(users) if valid
   * Otherwise return with error message
   * @param users
   * @return
   */
  def checkUsers(users: Option[Seq[String]]) = {
    users match {
      case Some(us: Seq[String]) =>
        if ((us.length < 2) || (us.length > 100))
          Some("Users' length must be at least 2 and at most 100")
        else
          Some(us.sortWith(_ < _))
      case None => Some("Users are required")
      case _ => Some("Invalid users")
    }
  }

  /**
   * Check privacy
   * Valid values: private, public
   * Return None if privacy is valid
   * Otherwise return with error message
   * @param privacy
   * @return
   */
  def checkPrivacy(privacy: Option[String]): Either[String, String] = {
    privacy match {
      case Some("private") => Right("private")
      case Some("public") => Right("public")
      case None => Right("No privacy is found")
      case _ => Left("Invalid privacy")
    }
  }

  /**
   * Validate room
   * Valid users length: 2 - 100
   * Check duplication
   * @param jv JsValue
   * @return Option[String]
   */
  def validateRoom(creator: String, jv: JsValue): Option[String] = {
    val users: Seq[String] = (jv \ "users").asOpt[Seq[String]].getOrElse(Seq())

    if ((users.toSet.size < 2) || (users.toSet.size > 100))
      Some("Number of users must be at least 2 and at most 100")
    else if (users.toSet.size != users.size)
      Some("Users must not be duplicated")
    else
      None
  }

}
