package consts

/**
 * Created by z00036 on 2015/06/14.
 */
trait UserConsts {

  abstract sealed class AuthLevel(val value: Int, val name: String)
  object AuthLevel {
    case object Admin extends AuthLevel(0, "ADMIN")
    case object User extends AuthLevel(1, "USER")
    case object ExternalUser extends AuthLevel(2, "EXTERNAL_USER")
  }

  abstract sealed class UserType(val value: Int, val name: String)
  object UserType {
    case object Email extends UserType(1, "EMAIL")
    case object Domain extends UserType(2, "DOMAIN")
  }
}
