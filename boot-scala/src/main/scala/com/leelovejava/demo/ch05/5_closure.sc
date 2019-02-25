// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

def mulBy(factor : Double) = (x : Double) => factor * x

val triple = mulBy(3)
val half = mulBy(0.5)
println(triple(14) + " " + half(14))


