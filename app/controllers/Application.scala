package controllers
import actors.GoogleStorer
import akka.actor.Props
import models.SearchTerm
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import lib.Writers._
import play.libs.Akka
import services._
import services.mongodb.{MongoDbWatchListService, MongoDbGoogleResultService}
import scala.concurrent.duration._
import forms._

class Application extends Controller {

  val google = new GoogleResultFetcher
  val actor = Akka.system.actorOf(Props[GoogleStorer], name = "Storer")
  Akka.system.scheduler.schedule(0.seconds, 60.seconds, actor, Nil)

  val results = new MongoDbGoogleResultService
  val words = new MongoDbWatchListService

  def current(tld: String, query: String) = Action.async {
    val image = google.getResults(s"https://www.google.$tld?q=$query")
    image map { result => Ok(Json.toJson(result)) }
  }

  def addWatch() = Action { implicit request =>
    val id = AddWatchWord.bindFromRequest().get
    words.add(id)
    Redirect("/")
  }

  def removeWatch() = Action { implicit request =>
    val term = RemoveWatchWord.bindFromRequest().get
    words.removeById(term)
    Redirect("/")
  }


  def index(country: String) = Action.async {
    for {
      watchList <- words.getAll
      countries <- TrendingSearchTerms.getListOfCountries()
      countryResult <- Trending.getCountryResult(country)
    } yield Ok(views.html.index(watchList.map{a =>(a.id, a.searchTerm.query)}, countryResult, countries,
      tldMapping.getOrElse(country, "com")))
  }

  def term(term: String, tld: String) = Action.async {
    for {
      results <- results.getByTerm(SearchTerm(tld, term))
      hi = println(results.head.googleResult.report)
    } yield Ok(views.html.dashboard(results))
  }

  val tldMapping = Map("united_kingdom" -> "co.uk",
  "united_states" -> "com",
  "romania" -> "ro",
  "finland" -> "fi",
  "portugal" -> "po",
  "memxico" -> "mx",
  "egypt" -> "eg",
  "brazil" -> "bz",
  "india" -> "in",
  "malaysia" -> "my")
}
