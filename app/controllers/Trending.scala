package controllers

import models.InTheNewsLink
import services.{GoogleResultFetcher, TrendingSearchTerms, GoogleResultService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TermResult(term: String, inTopBox: Boolean)
case class CountryResult(country: String, termResults: List[TermResult])

object Trending {

  val google = new GoogleResultFetcher

  def containsGuardian(report: Seq[InTheNewsLink]): Boolean = {
    report.exists(_.href.contains("guardian"))
  }

  def getCountryResult(countryString: String) : Future[CountryResult] =  {
    TrendingSearchTerms.get().map(_.toList)
      .flatMap { list =>
        Future.sequence(list.flatMap { case (country, terms) =>
          if (country.equals(countryString)) {
            Some(Future.sequence(terms.map { term =>
              inTopBox(term).map(bool => TermResult(term, bool))
            }).map(list => CountryResult(country, list)))
          } else {
            None
          }

        })
      }.map{_.head}
  }

  def inTopBox(term: String): Future[Boolean] = {
    Future(math.random < 0.25)
//    google.getResults(s"https://www.google.com?q=$term")
//      .map(result => containsGuardian(result.report))
  }

}
