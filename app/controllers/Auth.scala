package controllers

import consts._
import models.{AuthInfo, Auth => AuthModel}
import org.joda.time.DateTime
import play.api._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.api.data._
import play.api.data.validation._
import play.api.data.Forms._
import play.api.libs.Crypto._
import utils.RequestHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

/**
 * Created by z00036 on 2015/06/08.
 */
object Auth extends Controller {

  val emailPattern = Constraints.pattern("[\\w\\d_-]+@[\\w\\d_-]+\\.[\\w\\d._-]+".r)

  val loginForm = Form(tuple(
    "email" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(emailPattern)
  ))

  def login = Action { implicit request =>
    implicit val auth: Option[AuthInfo] = None
    Ok(views.html.login(None))
  }

  def authenticate = Action { implicit request =>
    val url =
      s"""https://accounts.google.com/o/oauth2/auth?
         |client_id=${AppConfig.google.auth.client.id}&
         |response_type=code&
         |scope=email%20profile&
         |redirect_uri=${request.hostUrl}${AppConfig.google.auth.redirect.url}""".stripMargin
    Redirect(url.replace(Properties.lineSeparator, ""))
  }

  /**
   * Googleからのコールバック
   * @param code
   * @return
   */
  def authCallback(code: String) = Action.async { implicit request =>
    AuthModel.getEmail(code).map { authInfo =>
      Redirect(routes.Application.index).withCookies(
        Cookie(CookieKeys.AUTH_INFO, authInfo.toCookieString, secure = true)
      )
    }
  }

  /**
   * ログインエラー時に返す画面
   * @return
   */
  def onLoginError = {
    implicit val auth: Option[AuthInfo] = None
    BadRequest(views.html.login(Option(m"error.login")))
  }

  /**
   * IPASSでログインしたときのもの
   * @return
   */
  def ipassLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => onLoginError,
      success => AuthModel.loginByIpass(success._1, success._2).fold(onLoginError){ implicit authInfo =>
        Redirect(routes.Application.index).withCookies(
          Cookie(CookieKeys.AUTH_INFO, authInfo.toCookieString, secure = true)
        )
      }
    )
  }

}
