// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

/*class Pair[T](val first: T, val second: T) {
  def smaller = if (first.compareTo(second) < 0) first else second // Error
}*/

class Pair1[T <: Comparable[T]](val first: T, val second: T) {
  def smaller = if (first.compareTo(second) < 0) first else second
}

val p = new Pair1("Fred", "Brooks")
println(p.smaller)

val pe = new Pair(4, 2) // Won't work--see Section 17.4 for a remedy
//println(pe.smaller)

class Pair2[T](val first: T, val second: T) {
  def replaceFirst[R >: T](newFirst: R) = new Pair(newFirst, second)
  override def toString = "(" + first + "," + second + ")"
}

class Person1(name: String) {
  override def toString = getClass.getName + " " + name
}

class Student1(name: String) extends Person1(name)

val fred = new Student1("Fred")
val wilma = new Student1("Wilma")
val barney = new Person1("Barney")

val p1 = new Pair2(fred, wilma)
p1.replaceFirst(barney) // A Pair[Person]

// Don't omit the upper bound:

class Pair3[T](val first: T, val second: T) {
  def replaceFirst[R](newFirst: R) = new Pair(newFirst, second)
  override def toString = "(" + first + "," + second + ")"
}

val p2 = new Pair3(fred, wilma)
p2.replaceFirst(barney) // A Pair[Any]

