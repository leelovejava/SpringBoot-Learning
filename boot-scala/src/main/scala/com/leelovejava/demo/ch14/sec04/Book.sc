class Document {
  import scala.collection.mutable._
  type Index = HashMap[String, (Int, Int)]
}

class Book extends Document {
  val tables = new Index
  val figures = new Index

  def addTableRef(title: String, chapter: Int, section: Int) {
    tables += title -> (chapter, section)
  }

  def addFigureRef(caption: String, chapter: Int, section: Int) {
    figures += caption -> (chapter, section)
  }
}
