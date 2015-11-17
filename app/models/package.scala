package object models {
  case class InTheNewsLink(rank: Int, title: String, href: String)
  case class GoogleResult(image: String, report: Seq[InTheNewsLink])

}
