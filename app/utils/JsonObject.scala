package utils

import scala.annotation.tailrec
import scala.util.Try
import rapture._
import json._, data._, jsonBackends.jawn._
import play.api.libs.json.{ Json => PJson, _ }
import play.api.libs.json.JsObject
import rapture.data.Extractor
import rapture.data.BasicExtractor
import rapture.data.Serializer

/**
 * rapture.json.JsonがWSResponseと相性悪いらしく、
 * 使用クラス内でimportすると動かなくなるので、parseだけ別objectに分割
 */
object JsonObject {
  /**
   * Json変換可能オブジェクトの定義
   */
  trait Jsonable {
    def seed = serialize.seed
    def toJson: JsonBuffer

    override def toString: String = {
      import formatters.humanReadable
      JsonBuffer.format(toJson)
    }

    def toCompactString: String = {
      import formatters.compact
      JsonBuffer.format(toJson)
    }

    /**
     * ちょっと楽するためのメソッド
     */
    protected def mkJson(mk: JsonBuffer => Unit) = {
      val js = seed
      mk(js)
      js
    }

    implicit class seqSupport(js: JsonBuffer) {
      def set[A](values: Seq[A])(f: (JsonBuffer, Int, A) => Unit) = values.zipWithIndex.foreach { case (value, index) =>
        f(js, index, value)
      }
      def setArray[A <: Jsonable](key: String, values: Seq[A]) = values.zipWithIndex.foreach { case (value, index) =>
        js.selectDynamic(key)(index) = value.toJson
      }
    }

    implicit class optSupport(js: JsonBuffer) {
      def opt[A <% ForcedConversion[JsonBuffer]](key: String, value: Option[A]) = value.foreach(v => js.updateDynamic(key)(v))
      def optFold[A](value: Option[A])(fEmpty: (JsonBuffer) => Unit)(fExists: (JsonBuffer, A) => Unit) = value.fold {
        fEmpty(js)
      }{ obj =>
        fExists(js, obj)
      }
      def optSeq[A <: Jsonable](key: String, valueOp: Option[Seq[A]]) = valueOp.foreach { values =>
        js.setArray(key, values)
      }
    }
  }

  /**
   * Jsonオブジェクトに変換する
   */
  def parse(text: String) = Json.parse(text)

  /**
   * Jsonパースして、Tryで返す
   */
  def tryParse(text: String) = {
    import core.modes.returnTry
    Json.parse(text)
  }

  /**
   * JsonBufferを作成する
   */
  def make(f: JsonBuffer => Unit): JsonBuffer = {
    val js = JsonBuffer.empty
    f(js)
    js
  }

  /**
   * シリアライズ系
   */
  object serialize {

    /**
     * シリアライズの元
     */
    def seed = JsonBuffer.empty

    /**
     * Jsonableをシリアライズする
     */
    def apply[A <: Jsonable](obj: A): JsonBuffer = obj.toJson

    /**
     * Seqをシリアライズする
     */
    def apply[A <: Jsonable](ary: Seq[A]): JsonBuffer = {
      val js = seed
      ary.zipWithIndex.foreach { obj =>
        js(obj._2) = apply(obj._1)
      }
      js
    }

    def applySeqOp[A <: Jsonable](aryOp: Option[Seq[A]]):Option[Seq[JsonBuffer]] ={
      val seeds: Seq[JsonBuffer] = Nil
      aryOp.map(ary => applySeq(ary))
    }

    /**
     * Seqをシリアライズする
     */
    def applySeq[A <: Jsonable](ary: Seq[A]): Seq[JsonBuffer] = {
      val seeds: Seq[JsonBuffer] = Nil
      recursion(seeds, 0, ary)
    }

    @tailrec
    private def recursion[A <: Jsonable](seeds: Seq[JsonBuffer], index: Int, ary: Seq[A]): Seq[JsonBuffer] = {
      if (ary.isDefinedAt(index)) {
        val jsSeq = seeds :+ apply(ary(index))
        recursion(jsSeq, index + 1, ary)
      } else {
        seeds
      }
    }

    /**
     * Optionをシリアライズする
     */
    def apply[A <: Jsonable](opt: Option[A]): JsonBuffer = opt.fold(seed)(_.toJson)

  }
}