// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

import scala.math._

// 通过View的懒执行
val palindromicSquares = (1 to 100).view.map(x => { println(x) ; x * x })

// Evaluates the first eleven
palindromicSquares.take(10).mkString(",")

// Contrast with streams
def squares(x: Int): Stream[Int] = { println(x) ; x * x } #:: squares(x + 1)

val palindromicSquareStream = squares(0)


palindromicSquareStream(10)

// Caution

// Evaluates only the first ten
palindromicSquares.take(10).last

// Evaluates all elements
palindromicSquares(10)
