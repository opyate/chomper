package com.opyate
package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */

object DigitalAsset extends DigitalAsset with LongKeyedMetaMapper[DigitalAsset] {

  override def dbTableName = "digital_asset" // define the DB table name

}

class DigitalAsset extends LongKeyedMapper[DigitalAsset] {
  def getSingleton = DigitalAsset

  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object inventory_id extends LongMappedMapper(this, DigitalMeta)
  object ass_path extends MappedText(this)
  object mime_type  extends MappedString(this, 100)
  object sort_order extends MappedInt(this)



}