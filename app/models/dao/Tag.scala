package models.dao

import scalikejdbc._
import skinny.orm._
import org.joda.time._

/**
 * Created by z00036 on 2015/06/12.
 */
case class Tag(
  id: Int,
  name: String,
  color: String,
  update_time: String
)


object Tag extends SkinnyCRUDMapper[Tag] {
  override def defaultAlias = createAlias("t")
  override def extract(rs: WrappedResultSet, rn: ResultName[Tag]) = autoConstruct(rs, rn)

  def insert(name: String, color: String) = {
    val id = this.createWithAttributes(
      'name -> name,
      'color -> color
    )
    findById(id)
  }
}

