package consts

/**
 * Created by z00036 on 2015/06/14.
 */
trait ErrorConsts {

  sealed abstract class ErrorCode(val code: Int, val logMessage: String, val userMessage: String)
  object ErrorCode {
    case object LoginError extends ErrorCode(401, "ログイン失敗", "error.login")
    case object AuthError extends ErrorCode(401, "認証情報がありません", "error.auth")
  }
}
