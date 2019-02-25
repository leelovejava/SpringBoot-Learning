// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 02_char.sc to run them all at once.

import scala.io._

val source = Source.fromFile("mary.txt", "UTF-8")

var upper = 0
for (c <- source) if (c.isUpper) upper += 1
println(f"$upper uppercase")

val source2 = Source.fromFile("mary.txt", "UTF-8")
val iter = source2.buffered
while (iter.hasNext) {
  if (iter.head.isUpper) {     
    while (iter.hasNext && !iter.head.isWhitespace)
      print(iter.next)
    println()
  }
  else iter.next
}
source.close()

val source3 = Source.fromFile("mary.txt", "UTF-8")
val contents = source3.mkString
println(f"${contents.substring(0, 10)}...")

