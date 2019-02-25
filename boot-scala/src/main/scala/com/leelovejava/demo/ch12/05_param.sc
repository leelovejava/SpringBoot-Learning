// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

case class Delimiters(left: String, right: String)

def quote(what: String)(implicit delims: Delimiters) =
  delims.left + what + delims.right

quote("Bonjour le monde")(Delimiters("«", "»"))

object FrenchPunctuation {
  implicit val quoteDelimiters = Delimiters("«", "»")
}

import FrenchPunctuation._
// import FrenchPunctuation.quoteDelimiters

quote("Bonjour le monde")

