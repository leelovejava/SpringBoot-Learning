// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 03_unit.sc to run them all at once.

import scala.io._

val source = Source.fromFile("values.txt", "UTF-8")
val tokens = source.mkString.split("\\s+")
val numbers = for (w <- tokens) yield w.toDouble
println("Sum: " + numbers.sum)

print("How old are you? ")
val age = StdIn.readInt()
println("Next year, you will be " + (age + 1))
