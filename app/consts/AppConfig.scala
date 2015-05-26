package consts

import scala.language.dynamics
import com.typesafe.config.ConfigFactory
import play.api.Configuration

/**
 * Created by z00036 on 2015/05/25.
 */
/**
 * 設定ファイルから値を取得する
 * 「AppConfig.cacoo.yabumoto.apiKey」とかで書ける
 * 「AppConfig.cacoo("yabumoto").apiKey」とでも書ける
 */
object AppConfig extends Dynamic {

  val config = Configuration(ConfigFactory.load())

  def apply(key: String*): AppConfig = new AppConfig(key: _*)
  def selectDynamic(key: String): AppConfig = new AppConfig(key)
  def applyDynamic(key: String)(index: String): AppConfig = new AppConfig(key, index)

  sealed abstract class Mode(val name: String) {
    override def toString: String = name
  }
  object Mode {
    case object DEBUG extends Mode("DEBUG")
    case object RELEASE extends Mode("RELEASE")
    def apply(name: String) = name match {
      case "DEBUG"   => DEBUG
      case "RELEASE" => RELEASE
    }
  }
}

/**
 * 設定取得用クラス
 */
class AppConfig private[consts] (val keys: String*) extends Dynamic {

  def apply(key: String*): AppConfig = {
    val newKeys = keys ++ key
    new AppConfig(newKeys: _*)
  }

  override def toString: String = getOrEmpty
  def get: Option[String] = AppConfig.config.getString(keys.mkString("."))
  def getOrEmpty: String = getOrElse("")
  def getOrElse[B >: String](default: => B): B = AppConfig.config.getString(keys.mkString(".")).getOrElse(default)
  def selectDynamic(key: String): AppConfig = new AppConfig((keys :+ key): _*)
  def applyDynamic(key: String)(index: String): AppConfig = new AppConfig((keys :+ key :+ index): _*)

  def asBoolean = get.fold(false)(_.toBoolean)
  def asInt = get.fold(0)(_.toInt)
  def mkString = get.mkString
}
