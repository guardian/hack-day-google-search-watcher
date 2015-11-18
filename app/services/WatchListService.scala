package services
import models.{SavedSearchTerm, SearchTerm}
import scala.concurrent.Future

trait WatchListService {
  def add(term: SearchTerm): Unit
  def removeById(id: String): Unit
  def getAll: Future[Seq[SavedSearchTerm]]
}
