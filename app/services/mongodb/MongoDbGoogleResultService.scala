package services.mongodb
import com.mongodb.casbah.Imports._
import models.{InTheNewsLink, GoogleResult}
import org.joda.time.DateTime
import services.GoogleResultService
import play.api.libs.concurrent.Execution.Implicits._


import scala.concurrent.Future

class MongoDbGoogleResultService extends GoogleResultService with MongoDbConnection
{
  private val collection = db("results")

  private def toDocument(r: GoogleResult) = {
    val report = r.report map {r => DBObject("href" -> r.href, "rank" -> r.rank, "title" -> r.title)}
    DBObject("image" -> "", "time" -> r.time.getMillis, "report" -> report)
  }

  def store(result: GoogleResult) = {
    collection.insert(toDocument(result))
  }

  def getAll = Future(for {
    doc <- collection.find().toSeq
    image <- doc.getAs[String]("image")
    time <- doc.getAs[Long]("time")
    report <- doc.getAs[Seq[DBObject]]("report")
    reportReal = report map { o =>
      InTheNewsLink(o.as[Int]("rank"), o.as[String]("href"), o.as[String]("title"))
    }
  } yield GoogleResult(image, new DateTime(time), reportReal))
}
