// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

def getMiddle[T](a: Array[T]) = a(a.length / 2)

getMiddle(Array("Mary", "had", "a", "little", "lamb"))

val f = getMiddle[String] _

f(Array("Mary", "had", "a", "little", "lamb"))
