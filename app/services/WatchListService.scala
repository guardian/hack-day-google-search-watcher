package services
import models.SearchTerm
import scala.concurrent.Future

trait WatchListService {
  def add(term: SearchTerm): Unit
  def getAll: Future[Seq[SearchTerm]]
}
