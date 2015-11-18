package services
import models.{SearchTermResult, SearchTerm, GoogleResult}

import scala.concurrent.Future

trait GoogleResultService
{
  def store(result: SearchTermResult): Unit
  def getAll: Future[Seq[SearchTermResult]]
  def getByTerm(term: SearchTerm): Future[Seq[SearchTermResult]]
  def getAllTermsWithResults: Future[Seq[SearchTerm]]
}
