package controllers

import play.api.mvc.{Action, Controller}
import services.{TrendingSearchTerms, GoogleResultService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TermResult(term: String, inTopBox: Boolean)
case class CountryResult(country: String, termResults: List[TermResult])

class Trending extends Controller {

  val google = new GoogleResultService



  def index(countryString: String) = Action.async {
    val trending = TrendingSearchTerms.get().map(_.toList).map{list =>
      list.map{case (country, terms) =>

      Future.sequence(terms.map{term =>
        google.getImage(s"https://www.google.com?q=$term")
        .map(result => result.report.nonEmpty).map(bool => TermResult(term, bool))
      }).map(list => CountryResult(country, list))

    }}



    trending.map(Ok(_))
  }

}
