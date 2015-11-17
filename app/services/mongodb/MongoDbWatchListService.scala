package services.mongodb
import models.SearchTerm
import services.WatchListService
import com.mongodb.casbah.Imports._
import play.api.libs.concurrent.Execution.Implicits._


import scala.concurrent.Future


class MongoDbWatchListService extends WatchListService with MongoDbConnection {

  private val watchList = db("watchList")
  def add(term: SearchTerm) = watchList.insert(DBObject("tld" -> term.tld, "query" -> term.query))

  def getAll = Future((for {
    obj <- watchList.find()
    tld <- obj.getAs[String]("tld")
    term <- obj.getAs[String]("query")
  } yield SearchTerm(tld, term)).toSeq)
}
