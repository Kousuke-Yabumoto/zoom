package models

import consts.AppConfig
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import play.api.libs.ws._
import play.api.mvc.Request
import utils.RequestHelper._
import utils.JsonObject
import models.dao.{User => UserDao}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

/**
 * Created by z00036 on 2015/06/09.
 */
object Auth {

  def getEmail(code: String)(implicit request: Request[_]): Future[AuthInfo] = {
    for {
      token <- getToken(code)
      authInfo <- getEmail(token)
    } yield authInfo
  }

  /**
   * トークン取得
   * @param code
   * @param request
   * @return
   */
  private def getToken(code: String)(implicit request: Request[_]): Future[AuthInfo.Token] = {
    WS.url(s"""https://accounts.google.com/o/oauth2/token""").post(
      Map(
        "code" -> Seq[String](code),
        "client_id" -> Seq[String](AppConfig.google.auth.client.id),
        "client_secret" -> Seq[String](AppConfig.google.auth.client.secret),
        "redirect_uri" -> Seq[String](s"${request.hostUrl}${AppConfig.google.auth.redirect.url}"),
        "grant_type" -> Seq[String]("authorization_code")
      )
    ).map { response =>
      JsonObject.tryParse(response.body).map { js =>
        js.as[AuthInfo.Token]
      }.getOrElse {
        val errorString = s"""Google認証エラー：トークン取得レスポンスが不正です${Properties.lineSeparator}${response.body}"""
        throw new Exception(errorString)
      }
    }
  }

  /**
   * Emailアドレスの取得
   * @param token
   * @param request
   * @return
   */
  private def getEmail(token: AuthInfo.Token)(implicit request: Request[_]) = {
    WS.url(s"https://www.googleapis.com/plus/v1/people/me").withHeaders {
      "Authorization" -> (s"Bearer ${token.access_token}")
    }.get().map { response =>
      val me = JsonObject.tryParse(response.body).map { js =>
        js.as[AuthInfo.Me]
      }.getOrElse {
        val errorString = s"""Google認証エラー：トークン取得レスポンスが不正です${Properties.lineSeparator}${response.body}"""
        throw new Exception(errorString)
      }
      AuthInfo(me)
    }
  }

  /**
   * IPASSログイン
   * @param email
   * @param password
   * @return
   */
  def loginByIpass(email: String, password: String) = UserDao.loginUser(email, Option(password)).map { user =>
    AuthInfo(user)
  }

}
