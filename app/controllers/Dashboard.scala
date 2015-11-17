package controllers

import play.api.mvc.{Action, Controller}
import services.{GoogleResultFetcher, GoogleResultService}
import scala.concurrent.ExecutionContext.Implicits.global

class Dashboard extends Controller {

  val google = new GoogleResultFetcher()


  def index(query: String) = Action.async {
    val futureResult = google.getResults(s"https://www.google.com?q=$query")
    futureResult.map(result => Ok(views.html.dashboard(result.image)))
  }

}
