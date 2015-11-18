package controllers
import actors.GoogleStorer
import akka.actor.Props
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
      hi = println(watchList)
      //countryResult <- Trending.getCountryResult(country)
    } yield Ok(views.html.index(watchList.map{a =>(a.id, a.searchTerm.query)}, CountryResult("GB", Nil)))
  }

  def countries() = Action.async{
    TrendingSearchTerms.getListOfCountries().map(list => Ok(views.html.countryTrending(list)))
  }

  def term(id: String) = Action.async {
    for {
      term <- words.getById(id)
      results <- results.getByTerm(term)
    } yield Ok(results.toString())
  }
}
