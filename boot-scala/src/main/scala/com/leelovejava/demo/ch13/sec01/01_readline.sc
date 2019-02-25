// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 01_readline.sc to run them all at once.

import scala.io.Source

val source = Source.fromFile("mary.txt", "UTF-8")
val lineIterator = source.getLines

for (l <- lineIterator) 
  println(if (l.length <= 13) l else l.substring(0, 10) + "...")

source.close()

// Caution: The sources below aren't closed.

val lines = Source.fromFile("mary.txt", "UTF-8").getLines.toArray

val contents = Source.fromFile("mary.txt", "UTF-8").mkString


