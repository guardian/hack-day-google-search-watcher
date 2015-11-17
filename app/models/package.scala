package object models {
  case class InTheNewsLink(rank: Int, title: String, href: String){
    def source: String = {
      href.split('/')(2)
    }
  }
  case class GoogleResult(image: String, report: Seq[InTheNewsLink])

}
