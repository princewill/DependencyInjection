package service

import play.api.cache._
import play.api.libs.json.Json
import scala.concurrent.duration._

import scala.collection.mutable

case class Book(id: Int, title: String)

object Book {
  implicit val jsonFormat = Json.format[Book]
}

trait BooksService {
  def list: Seq[Book]
  def get(id: String): Option[Book]
  def save(book: Book): Unit
}
class CachingBookingService(cache: CacheApi) extends BooksService {

  private val db = mutable.Map(1 -> Book(1, "Twilight"), 2 -> Book(2, "Tron"))


  override def get(id: String) = ???

  override def list: Seq[Book] = cache.getOrElse("books") {
    def freshBooks = fetchFreshBooks()
    cache.set("books", freshBooks, 2.minutes)
    freshBooks
  }

  private def fetchFreshBooks(): Seq[Book] = db.values.toSeq.sortBy(_.id)

  override def save(book: Book) = ???
}
