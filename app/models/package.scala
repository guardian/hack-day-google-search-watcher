import org.joda.time.DateTime

package object models {
  case class InTheNewsLink(rank: Int, title: String, href: String){
    def source: String = {
      href.split('/')(2)
    }
  }
  case class GoogleResult(image: String, time: DateTime, report: Seq[InTheNewsLink])
  case class SearchTerm(tld: String, query: String)

}
