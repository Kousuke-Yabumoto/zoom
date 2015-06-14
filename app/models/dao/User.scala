package models.dao

import consts._
import org.apache.commons.codec.digest.DigestUtils
import scalikejdbc._
import skinny.orm._
import org.joda.time._

/**
 * Created by z00036 on 2015/06/14.
 */
case class User(
  id: Int,
  email: String,
  password: String,
  displayName: String,
  userType: Int,
  authLevel: Int,
  update_time: String
)


object User extends SkinnyCRUDMapper[User] {
  override def defaultAlias = createAlias("u")
  override def extract(rs: WrappedResultSet, rn: ResultName[User]) = autoConstruct(rs, rn)

  /**
   * 全てのドメインユーザーを取得する
   * @return
   */
  def allDomains: List[User] = where(sqls.eq(User.column.userType, UserType.Domain.value)).apply()

  /**
   * ログインするユーザーを取得する
   * @param email
   * @param passwordOpt
   * @return
   */
  def loginUser(email: String, passwordOpt: Option[String] = None) = {
    email.split('@').lastOption.flatMap { domain =>
      allDomains.find(_.email == domain)
    }.orElse {
      passwordOpt.flatMap { password =>
        loginByIpass(email, password)
      }
    }
  }


  /**
   * ログインするユーザーを取得する
   * @param email
   * @param password
   * @return
   */
  def loginByIpass(email: String, password: String): Option[User] = {
    where(
      sqls.eq(User.column.email, email).and.eq(User.column.password, DigestUtils.sha256Hex(password))
    ).limit(1).apply().headOption
  }
}
