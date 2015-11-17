package actors
import akka.actor.Actor
import services.GoogleResultFetcher
import play.api.libs.concurrent.Execution.Implicits._
import services.mongodb.{MongoDbWatchListService, MongoDbGoogleResultService}

class GoogleStorer extends Actor {

  private val googleService = new GoogleResultFetcher
  private val storeService = new MongoDbGoogleResultService
  private val watchListService = new MongoDbWatchListService

  override def receive: Receive = {
    // the initial for-comprehension flatMap gets in the way here
    case _ => watchListService.getAll.map {list => list map {item =>
      googleService.getResults(s"https://google.${item.tld}?q=${item.query}")
    }}
  }
}
