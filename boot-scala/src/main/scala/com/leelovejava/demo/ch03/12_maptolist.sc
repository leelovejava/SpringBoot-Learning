// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

val names = List("Peter", "Paul", "Mary")

// map 映射
names.map(_.toUpperCase) // List("PETER", "PAUL", "MARY")

for (n <- names) yield n.toUpperCase

def ulcase(s: String) = Vector(s.toUpperCase(), s.toLowerCase())

names.map(ulcase)

// flatmap映射
names.flatMap(ulcase)

names.foreach(println)


