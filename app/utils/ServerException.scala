package utils

import consts._
import scala.util.Properties

/**
 * Created by z00036 on 2015/06/14.
 */
class ServerException(val code: Int, val userMessage: String, val logMessage: String, ex: Option[Throwable] = None)
  extends Exception(
    s"${code} ${userMessage}:${logMessage} ${ex.map { e => s"${e.getMessage}${ServerException.r}${e.getStackTrace.mkString(ServerException.r)}"}}")

object ServerException {
  val r = Properties.lineSeparator
  def apply(code: ErrorCode): ServerException = new ServerException(code.code, code.userMessage, code.logMessage)
}