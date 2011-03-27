package com.opyate
package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */

object DigitalMeta extends DigitalMeta with LongKeyedMetaMapper[DigitalMeta] {

  override def dbTableName = "digital_meta" // define the DB table name

  def getItemDetails(id: Int): ((Long, String), String)= {
    val item= getItem(id)
    item.zip(getDigitalAsset(id)) match {
      case head :: tail => head
      case Nil => ((1L,"N/A"), "N/A")
    }
  }

  def getItem(id: Int): List[(Long, String)]= {
    DigitalMeta.findAllFields(Seq[SelectableField] (
          DigitalMeta.id, DigitalMeta.name),
          By(DigitalMeta.id, id)
          ).map{
            row => println(row);(row.id.is, row.name.is )
          }
  }

  def getDigitalAsset(id: Int): List[String]= {
    DigitalAsset.findAllFields(Seq[SelectableField](DigitalAsset.ass_path),
          By(DigitalAsset.inventory_id, id)
          ).map{
            row => row.ass_path.is
          }
  }



}

class DigitalMeta extends LongKeyedMapper[DigitalMeta] with OneToMany[Long, DigitalMeta]{

  def getSingleton = DigitalMeta
  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 250) {
    override def dbIndexed_? = true
    override def setFilter = trim _ :: toUpper _ :: super.setFilter
  }

  object digital_asset extends MappedOneToMany(
    DigitalAsset, DigitalAsset.inventory_id, OrderBy(
      DigitalAsset.id, Ascending
    )
  )

  object remote_location extends MappedString(this, 4096) {
    override def dbIndexed_? = true
    override def setFilter = trim _ :: toLower _ :: super.setFilter
  }
  
}
