import java.net.URI

import org.joda.time.DateTime

package object models {
  case class InTheNewsLink(rank: Int, title: String, href: String){
    def source: String = {
      href.split('/')(2)
    }
    def hostname = new URI(href).getHost
  }
  case class SearchTerm(tld: String, query: String)
  case class SavedSearchTerm(id: String, searchTerm: SearchTerm)

  case class GoogleResult(image: String, time: DateTime, report: Seq[InTheNewsLink])
  case class SearchTermResult(searchTerm: SearchTerm, googleResult: GoogleResult)
}
