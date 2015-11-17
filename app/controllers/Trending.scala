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
    val trending = TrendingSearchTerms.get().map(_.toList)
      .flatMap { list =>
        Future.sequence(list.flatMap { case (country, terms) =>
          if (country.equals(countryString)) {
            Some(Future.sequence(terms.map { term =>
              google.getImage(s"https://www.google.com?q=$term")
                .map(result => result.report.nonEmpty).map(bool => TermResult(term, bool))
            }).map(list => CountryResult(country, list)))
          } else {
            None
          }

        })
      }.map{_.head}

    Future(Ok("test"))
  }

}
