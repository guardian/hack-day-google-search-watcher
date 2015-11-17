package services

import play.api.libs.json.JsValue
import play.api.libs.ws.WS

import scala.concurrent.Future
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Execution.Implicits._
/**
  * Created by mahanaclutha on 12/11/15.
  */
object TrendingSearchTerms {

  def get(): Future[Map[String, List[String]]] = {
    val futureJsValue = WS.url("https://www.google.com/trends/hottrends/visualize/internal/data").get().map(response => response.json)
    futureJsValue.map{ json =>
      getCountries(json).map{country =>
        val terms: List[String] = (json \ country).as[List[String]]
        (country, terms)
      }.toMap
    }
  }

  def getCountries(json: JsValue): List[String] = {
    json.toString()
      .split(",")
      .filter(_.contains(":"))
      .map(string => string.split(":")(0))
      .map(string => string.replace("{", "").replace("\"", "")).toList
  }

}
