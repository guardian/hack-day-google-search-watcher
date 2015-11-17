package controllers
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import lib.Writers._
import services._

class Application extends Controller {

  val google = new GoogleResultService

  def current(tld: String, query: String) = Action.async {
    val image = google.getImage(s"https://www.google.$tld?q=$query")
    image map { result => Ok(Json.toJson(result)) }
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
}
