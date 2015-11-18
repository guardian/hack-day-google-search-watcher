package services.mongodb
import com.mongodb.casbah.Imports._
import models.{SearchTerm, SearchTermResult, InTheNewsLink, GoogleResult}
import org.joda.time.DateTime
import services.GoogleResultService
import play.api.libs.concurrent.Execution.Implicits._


import scala.concurrent.Future

class MongoDbGoogleResultService extends GoogleResultService with MongoDbConnection
{
  private val collection = db("results")

  private def fromCursor(cursor: MongoCursor) = for {
    doc <- cursor.toList
    tld <- doc.getAs[String]("tld")
    term <- doc.getAs[String]("term")
    image <- doc.getAs[String]("image")
    time <- doc.getAs[Long]("time")
    report <- doc.getAs[Seq[DBObject]]("report")
    reportReal = report map { o =>
      InTheNewsLink(o.as[Int]("rank"), o.as[String]("title"), o.as[String]("href"))
    }
  } yield SearchTermResult(SearchTerm(tld, term), GoogleResult(image, new DateTime(time), reportReal))


  private def toDocument(r: SearchTermResult) = {
    val report = r.googleResult.report map {r => DBObject("href" -> r.href, "rank" -> r.rank, "title" -> r.title)}
    DBObject(
      "image" -> r.googleResult.image,
      "tld" -> r.searchTerm.tld,
      "term" -> r.searchTerm.query,
      "time" -> r.googleResult.time.getMillis,
      "report" -> report
    )
  }

  def getByTerm(term: SearchTerm) = Future(getByTermRecursive(term, 0))

  val maxTries = 100
  def getByTermRecursive(term: SearchTerm, prevTries: Int): List[models.SearchTermResult] ={
    if (prevTries == maxTries)
      throw new Exception("Exceeded max tries getting results for search term")
    val result = fromCursor(
      collection.find(DBObject("term" -> term.query, "tld" -> term.tld)).sort(DBObject("time" -> -1)))
    if (result.isEmpty){
      Thread.sleep(1000)
      getByTermRecursive(term, prevTries+1)
    }else{
      result
    }
  }

  def store(result: SearchTermResult) = {
    collection.insert(toDocument(result))
  }

  def getAll = Future(fromCursor(collection.find()))
  override def getAllTermsWithResults: Future[Seq[SearchTerm]] = for {
    terms <- getAll
  } yield terms.map(_.searchTerm).distinct
}
