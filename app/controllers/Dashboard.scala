package controllers

import play.api.mvc.{Action, Controller}

class Dashboard extends Controller {

  def index = Action {
    Ok(views.html.dashboard())
  }

}
