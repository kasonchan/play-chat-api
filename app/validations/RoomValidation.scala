package validations

/**
 * Created by ka-son on 5/31/15.
 */
trait RoomValidation {

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
      case Some(l: String) =>
        if ((l.length < 1) || (l.length > 50))
          Some("Username must be at least 1 character and at most 50 characters")
        else
          None
      case None => Some("A login is required")
      case _ => Some("Invalid login")
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
  def checkPrivacy(privacy: Option[String]): Option[String] = {
    privacy match {
      case Some("private") => None
      case Some("public") => None
      case None => Some("Privacy is required")
      case _ => Some("Invalid privacy")
    }
  }

}
