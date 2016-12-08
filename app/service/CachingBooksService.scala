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
  def get(id: Int): Option[Book]
  def save(book: Book): Unit
}
class CachingBooksService(cache: CacheApi) extends BooksService {

  private val db = mutable.Map(1 -> Book(1, "Twilight"), 2 -> Book(2, "Tron"))


  override def get(id: Int): Option[Book] = cache.getOrElse(s"book$id") {
    def freshBook = fetchFreshBook(id)
    cache.set(s"book$id", freshBook, 2.minutes)
    freshBook
  }

  private def fetchFreshBook(id: Int): Option[Book] = db.get(id)

  override def list: Seq[Book] = cache.getOrElse("books") {
    def freshBooks = fetchFreshBooks()
    cache.set("books", freshBooks, 2.minutes)
    freshBooks
  }

  private def fetchFreshBooks(): Seq[Book] = db.values.toSeq.sortBy(_.id)

  override def save(book: Book): Unit = {
    db(book.id) = book
  }
}
