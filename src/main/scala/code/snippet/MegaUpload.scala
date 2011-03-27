package com.opyate
package code.snippet

import com.opyate.code.model._

import java.io.{File,FileOutputStream}

import net.liftweb._
import util._
import common._
import Helpers._
import http._

/**
 * An uploader for digital assets and their meta data.
 */

class MegaUpload extends Logger {

  object links extends RequestVar(Links.create)

  //object fileName extends RequestVar[Box[String]](Full(Helpers.nextFuncName))

  def render ={
    // process the form
    def process() {

      (links.is.links_blob.is) match {
        case ("") => S.error("Please paste a bunch of links")
        case (links_blob) => {
          info("The RequestVar content is: %s".format(links_blob))
          info("Done")
        }
      }

    }


    "name=links_blob" #> SHtml.onSubmit(links.is.links_blob.set(_)) &
    "type=submit" #> SHtml.onSubmitUnit(process)
  }


}