package services.mongodb

import com.mongodb.casbah.Imports._

trait MongoDbConnection {
  private val client = MongoClient()
  protected val db = client("hack")

  protected val results = db("results")
  protected val watchWords = db("watchWords")
}
