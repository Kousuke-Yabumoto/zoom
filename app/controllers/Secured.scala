package controllers

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc.Results._
import play.api.libs.iteratee._
import play.api.mvc._

import consts._
import models.AuthInfo
import utils.ServerException

/**
 * Created by z00036 on 2015/06/14.
 */
object Secured {

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  /**
   * 認証処理を含めた部分アクション
   * @param f
   * @return
   */
  def apply(f: => AuthInfo => Request[AnyContent] => Result) = {
    authenticate(onUnauthorized) {
      user => Action { implicit request =>
        val res = f(user)(request)
        res.withCookies(Cookie(CookieKeys.AUTH_INFO, user.toCookieString))
      }
    }
  }

  /**
   * 非同期バージョンの認証処理込み
   * @param f
   * @return
   */
  def async(f: => AuthInfo => Request[AnyContent] => Future[Result]) = {
    authenticate(onUnauthorized) {
      user => Action.async { implicit request =>
        val res = f(user)(request)
        res.map(_.withCookies(Cookie(CookieKeys.AUTH_INFO, user.toCookieString)))
      }
    }
  }

  /**
   * 認証処理
   * @param onUnauthorized
   * @param action
   * @return
   */
  private def authenticate(onUnauthorized: RequestHeader => Result)(action: AuthInfo => EssentialAction): EssentialAction = {
    // headerに引数を絞り リクエスト処理へ返却
    EssentialAction { implicit request =>
      val cookieValue = request.cookies.get(CookieKeys.AUTH_INFO)
      if (cookieValue.isDefined) {
        action(AuthInfo(cookieValue.get.value))(request)
      } else {
        Done(onUnauthorized(request), Input.Empty)
      }
    }
  }
//    EssentialAction { implicit request =>
//      request.cookies.get(CookieKeys.AUTH_INFO).map{ cookie =>
//        Action { implicit r =>
//          f(AuthInfo(cookie.value))(r)
//        }
//      }.getOrElse {
//        throw ServerException(ErrorCode.AuthError)
//      }
//    }
//  }
}
