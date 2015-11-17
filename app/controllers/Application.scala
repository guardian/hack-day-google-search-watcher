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

  def index = Action.async {
    words.getAll map { words => Ok(views.html.index(words.map(_.query)))}
  }
}
