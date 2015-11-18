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
    doc <- collection.find().toSeq
    stuff = println(doc)
    tld <- doc.getAs[String]("tld")
    term <- doc.getAs[String]("term")
    image <- doc.getAs[String]("image")
    time <- doc.getAs[Long]("time")
    report <- doc.getAs[Seq[DBObject]]("report")
    reportReal = report map { o =>
      InTheNewsLink(o.as[Int]("rank"), o.as[String]("href"), o.as[String]("title"))
    }
  } yield SearchTermResult(SearchTerm(tld, term), GoogleResult(image, new DateTime(time), reportReal))


  private def toDocument(r: SearchTermResult) = {
    val report = r.googleResult.report map {r => DBObject("href" -> r.href, "rank" -> r.rank, "title" -> r.title)}
    DBObject(
      "image" -> "",
      "tld" -> r.searchTerm.tld,
      "term" -> r.searchTerm.query,
      "time" -> r.googleResult.time.getMillis,
      "report" -> report
    )
  }

  def getByTerm(term: SearchTerm) = Future(fromCursor(
    collection.find(DBObject("term" -> term.query, "tld" -> term.tld))
  ))

  def store(result: SearchTermResult) = {
    collection.insert(toDocument(result))
  }

  def getAll = Future(fromCursor(collection.find()))
}
