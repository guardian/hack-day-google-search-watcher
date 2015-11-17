package lib

import models.{GoogleResult, InTheNewsLink}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

object Writers {
  implicit val linkWrites: Writes[InTheNewsLink] = (
      (JsPath \ "rank").write[Int] and
      (JsPath \ "title").write[String] and
      (JsPath \ "href").write[String]
    )(unlift(InTheNewsLink.unapply))

  implicit val locationWrites: Writes[GoogleResult] = (
      (JsPath \ "image").write[String] and
      (JsPath \ "report").write[Seq[InTheNewsLink]]
    )(unlift(GoogleResult.unapply))
}
