package controllers

import play.api.mvc.{Action, Controller}
import services.GoogleResultService
import scala.concurrent.ExecutionContext.Implicits.global

class Dashboard extends Controller {

  val google = new GoogleResultService


  def index(query: String) = Action.async {
    val futureResult = google.getImage(s"https://www.google.com?q=$query")
    futureResult.map(result => Ok(views.html.dashboard(query, result)))
  }

}
