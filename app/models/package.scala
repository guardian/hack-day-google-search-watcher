import org.joda.time.DateTime

package object models {
  case class InTheNewsLink(rank: Int, title: String, href: String)
  case class GoogleResult(image: String, time: DateTime, report: Seq[InTheNewsLink])
  case class SearchTerm(tld: String, query: String)
}
