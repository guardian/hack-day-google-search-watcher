package services
import models.GoogleResult

import scala.concurrent.Future

trait GoogleResultService
{
  def store(result: GoogleResult): Unit
  def getAll: Future[Seq[GoogleResult]]
}
