package models

import consts._
import utils._
import play.api.libs.Crypto._
import models.dao.{User => UserDao}

/**
 * Created by z00036 on 2015/06/08.
 */
object AuthInfo {

  case class Token(
    access_token: String,
    expires_in: Int,
    token_type: String,
    id_token: String
  )

  case class Email(
    value: String
  )

  case class Me(
    emails: Seq[Email],
    displayName: String
  )

  /**
   * クッキーからのログイン
   * @param cookieAuth
   * @return
   */
  def apply(cookieAuth: String): AuthInfo = {
    val userInfo = decryptAES(cookieAuth).split(' ')
    if (userInfo.length != 3) throw ServerException(ErrorCode.AuthError)
    AuthInfo(
      userInfo(0),
      userInfo(1),
      userInfo(2).toInt
    )
  }

  /**
   * Google認証でユーザー作成
   * @param me
   * @return
   */
  def apply(me: Me): AuthInfo = {
    val infoOpt = for {
      email <- me.emails.headOption
      user <- UserDao.loginUser(email.value)
    } yield AuthInfo(
      email.value,
      me.displayName,
      user.authLevel
    )
    infoOpt.getOrElse(throw ServerException(ErrorCode.AuthError))
  }

  /**
   * IPASSログインしたとき
   * @param userDao
   * @return
   */
  def apply(userDao: UserDao): AuthInfo = AuthInfo(
    userDao.email,
    userDao.displayName,
    userDao.authLevel
  )
}

case class AuthInfo(
  email: String,
  displayName: String,
  authLevel: Int
) {
  override def toString = s"${email} ${displayName} ${authLevel}"
  def toCookieString = encryptAES(toString)
}
