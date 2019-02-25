// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

class Pair[T, S](val first: T, val second: S) {
  override def toString = "(" + first + "," + second + ")"
}

val p = new Pair(42, "String")

val p2 = new Pair[Any, Any](42, "String")
