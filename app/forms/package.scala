import models.SearchTerm
import play.api.data._
import play.api.data.Forms._

package object forms {
  val AddWatchWord = Form(
    mapping(
      "tld" -> text,
      "term" -> text
    )(SearchTerm.apply)(SearchTerm.unapply)
  )
  val RemoveWatchWord = Form(
    single("id" -> text)
  )
}
