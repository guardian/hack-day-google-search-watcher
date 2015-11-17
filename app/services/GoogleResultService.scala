package services

import models._
import org.openqa.selenium.Keys._
import org.openqa.selenium.OutputType._
import org.openqa.selenium.chrome.ChromeDriver
import scala.collection.JavaConversions._
import scala.concurrent._

class GoogleResultService(implicit e: ExecutionContext) {
  def getImage(url: String): Future[GoogleResult] = Future {
    val driver = new ChromeDriver

    driver.get(url)
    driver.getKeyboard.pressKey(ENTER)
    Thread sleep 1000

    val ss = driver.getScreenshotAs(BASE64)
    val links = driver.findElementsByXPath("//text()[contains(.,'In the news')]/../..//div[cite]/..//a")

    val report = links.toList.zipWithIndex.collect {
      case (link, index) if link.getAttribute("href") contains "guardian" =>
        InTheNewsLink(index +1, link.getText, link.getAttribute("href"))
    }

    driver.quit()
    GoogleResult(ss, report)
  }
}
