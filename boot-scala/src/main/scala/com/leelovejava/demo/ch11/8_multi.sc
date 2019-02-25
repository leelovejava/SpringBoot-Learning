// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 8_multi.sc to run them all at once.

// <:< allows to instantiate a class even though some methods
// may not be applicable

class Pair[T](val first: T, val second: T) {
  def smaller(implicit ev: T <:< Ordered[T]) =
    if (first < second) first else second
}

import java.net.URL

val p = new Pair(new URL("http://scala-lang.org"), new URL("http://horstmann.com"))
  // Ok as long as we don't call smaller

//p.smaller // Error

// <:< is used in the definition of the Option.orNull method 

val friends = Map("Fred" -> "Barney")
val friendOpt = friends.get("Wilma") // An Option[String]
val friendOrNull = friendOpt.orNull // A String or null

val scores = Map("Fred" -> 42)
val scoreOpt = scores.get("Fred") // An Option[Int]
//val scoreOrNull = scoreOpt.orNull // Error

// <:< can improve type inference

def firstLast[A, C <: Iterable[A]](it: C) = (it.head, it.last)

firstLast(List(1, 2, 3)) // Error

def firstLast[A, C](it: C)(implicit ev: C <:< Iterable[A]) =
  (it.head, it.last)

firstLast(List(1, 2, 3)) // OK

