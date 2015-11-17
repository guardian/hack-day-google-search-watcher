package controllers
import org.openqa.selenium.Keys._
import org.openqa.selenium.OutputType._
import org.openqa.selenium.chrome.ChromeDriver
import play.api.libs.concurrent.Execution.Implicits._
import scala.collection.JavaConversions._
import play.api.mvc._

import scala.concurrent.Future
case class InTheNewsLink(rank: Int, title: String, href: String)
case class GoogleResult(image: String, report: Seq[InTheNewsLink])

class Application extends Controller {

  def getImage(url: String): Future[GoogleResult] = Future {
    val driver = new ChromeDriver

    driver.get(url)
    driver.getKeyboard.pressKey(ENTER)
    Thread sleep 2000

    val ss = driver.getScreenshotAs(BASE64)
    val links = driver.findElementsByXPath("//text()[contains(.,'In the news')]/../..//div[cite]/..//a")

    val report = links.toList.zipWithIndex.collect {
      case (link, index) if link.getAttribute("href") contains "guardian" =>
        InTheNewsLink(index +1, link.getText, link.getAttribute("href"))
    }

    driver.quit()
    GoogleResult(ss, report)
  }

  def current(tld: String, query: String) = Action.async {
    val image = getImage(s"https://www.google.$tld?q=$query")
    image map { result => Ok(views.html.result(result.image, result.report)) }
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
}
