package lib

import models.{GoogleResult, InTheNewsLink}
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath}

object Readers {
  implicit val linkReads: Reads[InTheNewsLink] = (
      (JsPath \ "rank").read[Int] and
      (JsPath \ "title").read[String] and
      (JsPath \ "href").read[String]
    )(InTheNewsLink)

  implicit val locationReads: Reads[GoogleResult] = (
      (JsPath \ "image").read[String] and
      (JsPath \ "time").read[DateTime] and
      (JsPath \ "report").read[Seq[InTheNewsLink]]
    )(GoogleResult)
}
