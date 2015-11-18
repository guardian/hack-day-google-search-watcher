package services

import models._
import org.joda.time.DateTime
import org.openqa.selenium.Keys._
import org.openqa.selenium.OutputType._
import org.openqa.selenium.chrome.ChromeDriver
import scala.collection.JavaConversions._
import scala.concurrent._

class GoogleResultFetcher(implicit e: ExecutionContext) {
  def getResults(url: String): Future[GoogleResult] = Future {
    val driver = new ChromeDriver

    driver.get(url)
    driver.getKeyboard.pressKey(ENTER)
    Thread sleep 1500

    val ss = driver.getScreenshotAs(BASE64)
    val links = driver.findElementsByXPath("//text()[contains(.,'In the news')]/../..//div[cite]/..//a")

    val report = links.toList.zipWithIndex.collect {
      case (link, index) => InTheNewsLink(index +1, link.getText, link.getAttribute("href"))
    }

    driver.quit()
    GoogleResult(ss, DateTime.now, report)
  }
}
