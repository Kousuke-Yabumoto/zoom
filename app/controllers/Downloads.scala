package controllers

import java.io._

import org.joda.time.DateTime
import play.api._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by z00036 on 2015/05/21.
 */
object Downloads extends Controller {

  def movie = Action {
    /* デバッグコード */
    val movieFilePath = "/Users/z40008/develop/scala/cybrez-zoom/sample/penguin.mp4"
    /* デバッグコード */
    val movieFile = new File(movieFilePath)
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(movieFile)

    println(movieFile)

    Result(
      header = ResponseHeader(206,
        Map(
          CONTENT_LENGTH -> movieFile.length.toString,
          CONTENT_TYPE -> "video/mp4",
          LAST_MODIFIED -> DateTime.now().toString("EEE , d MMM yyyy HH:mm:ss z")
        )
      ),
      body = fileContent
    )
  }
}
