package utils

import play.api.mvc.Request

/**
 * Created by z00036 on 2015/06/09.
 */
object RequestHelper {

  implicit class RequestHelper(request: Request[_]) {

    def hostUrl = s"""${if (request.secure) "https://" else "http://"}${request.host}"""
  }
}
