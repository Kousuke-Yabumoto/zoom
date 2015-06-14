package models.dao

import scalikejdbc._
import skinny.orm._
import org.joda.time._

/**
 * Created by z00036 on 2015/05/25.
 */

case class Movie(
  id: Int,
  title: String,
  filename: Option[String],
  movie_url: Option[String],
  content_type: Option[String],
  information: Option[String],
  month: Int,
  tag: Int,
  auth_level: Int,
  status: Int,
  update_time: String
)

object Movie extends SkinnyCRUDMapper[Movie] {
  override def defaultAlias = createAlias("m")
  override def extract(rs: WrappedResultSet, rn: ResultName[Movie]) = autoConstruct(rs, rn)

  def insert(title: String, explain: String, fileName: Option[String], contentType: Option[String], url: Option[String]) = {
    val id = this.createWithAttributes(
      'title -> title,
      'filename -> fileName,
      'movie_url -> url,
      'information -> explain,
      'content_type -> contentType
    )
    findById(id)
  }

  def findByAuth(authLevel: Int, tag: Option[Int]= None, month: Option[Int] = None) = {
    val conditions = sqls.toAndConditionOpt(
      Option(sqls"auth_level & ${authLevel} = ${authLevel}"),
      tag.map(t => sqls.eq(Movie.column.tag, t)),
      month.map(m => sqls.eq(Movie.column.month, m))
    ).get // ※ひとつはOptionでくくってるものがあるので、必ず存在する
    where(conditions).apply()
  }

}
