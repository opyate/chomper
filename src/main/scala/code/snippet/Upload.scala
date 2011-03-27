package com.opyate
package code
package snippet


import com.opyate.code.model._

import scala.xml.{NodeSeq, Text, Elem}

import java.io.{File,FileOutputStream}

import net.liftweb._
import util._
import common._
import mapper._

import Helpers._
import http._
import S._
import js.JsCmds.Noop

/**
 * An uploader for digital assets and their meta data.
 */

class Upload extends Logger {

  object product extends RequestVar(DigitalMeta.create)
  object ass extends RequestVar(DigitalAsset.create)
  object assetFile extends RequestVar[Box[FileParamHolder]](Empty)


  /**
   * Use a unique name for the file, so you don't have
   * to rename assets before upload.
   *
   * If you want to use the file original name, use
   * fp.fileName
   *
   */
  object fileName extends RequestVar[Box[String]](Full(Helpers.nextFuncName))

  private def saveFile(fp: FileParamHolder): Unit = {
    fp.file match {
      case null =>
      case x if x.length == 0 => info("File size is 0")
      case x =>{
        info("We got a file!")

        /**
         * Change this path once you go on production
         */
        val filePath = "src/main/webapp/assets"



        /**
         * Set some fields on the asset table
         */
        fileName.is.map{
          name => ass.is.ass_path.set(filePath + "/" + name + fp.fileName.takeRight(4))
        }

        ass.is.mime_type.set(fp.mimeType)
        ass.is.sort_order.set(0)

        /**
         * This tell helps save the product_id
         * on the asset table, so you can keep the
         * relationship
         */

        product.is.digital_asset += ass
        product.is.save

        /**
         * Here we save the file to the File System
         * I'm 99% sure I could use open_! but I'd rather get
         * a broken link than a NPE
         */
        val oFile = new File(filePath,  fileName.is.openOr("BrokenLink") + fp.fileName.takeRight(4))
              val output = new FileOutputStream(oFile)
              output.write(fp.file)
              output.close()
        info("File uploaded!")
        S.notice("Thanks for the upload")
      }
    }
  }

  def render ={
    // process the form
    def process() {

      (assetFile.is, product.is.part_number.is) match {
        case (Empty, _) => S.error("You forgot to choose a file to upload")
        case (_, "") => S.error("You forgot to enter a part number")
        case (asset, partNumber) => {
          info("The RequestVar content is: %s".format(assetFile.is))
          assetFile.is.map{ info("About to start the file upload"); file => saveFile(file)}
          info("Done")
        }
      }

    }


    "name=part_number" #> SHtml.onSubmit(product.is.part_number.set(_)) &
    uploadAss &
    "type=submit" #> SHtml.onSubmitUnit(process)
  }


  def uploadAss: CssBindFunc = {
    /**
     * If it is a GET request, show the upload field,
     * else show a link to the image we just uploaded.
     */
    (S.get_?, assetFile.is, product.is.part_number.is) match {
      case (true, _, _)  => "name=digital_asset" #> SHtml.fileUpload(s => assetFile(Full(s)))
      case (_, Empty, _) => "name=digital_asset" #> SHtml.fileUpload(s => assetFile(Full(s)))
      case (_, _, "")    => "name=digital_asset" #> SHtml.fileUpload(s => assetFile(Full(s)))
      case (false, _, _) => "name=digital_asset" #> fileName.is.map{ name =>
        SHtml.link(
          "http://127.0.0.1:8080/assets/" + //Using open_! because we already made sure it is not Null
            name + assetFile.is.open_!.fileName.takeRight(4) ,
          () => Unit ,
          <span>Click to download asset: {name + assetFile.is.open_!.fileName.takeRight(4)}</span>
        )
      }
    }
  }


}
