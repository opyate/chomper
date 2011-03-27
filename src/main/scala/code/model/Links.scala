package com.opyate
package code.model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

/**
 * A blob of links that point to remote resources
 */

object Links extends Links with LongKeyedMetaMapper[Links] {

  override def dbTableName = "links" // define the DB table name

}

class Links extends LongKeyedMapper[Links] {
  def getSingleton = Links

  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object links_blob extends MappedText(this)
}