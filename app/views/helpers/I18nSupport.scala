package views.helpers

import play.api.i18n.{Messages, Lang}

/**
 * Created by z00036 on 2015/06/14.
 */
object I18nSupport {


  // メッセージ補助クラス
  implicit class MessageStringInterpolation(val sc: StringContext) extends AnyVal {

    def m(args: Any*)(implicit lang: Lang): String = Messages(sc.s(args: _*))

    def e(args: Any*)(implicit lang: Lang): String = Messages(s"error.${sc.s(args: _*)}")
  }

}
