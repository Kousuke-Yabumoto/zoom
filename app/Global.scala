
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.Play.current

/**
 * Created by z00036 on 2015/05/25.
 */
object Global extends GlobalSettings{

  override def onStart(app: Application) {
    skinny.DBSettings.initialize()
  }
}
