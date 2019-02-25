def foo[T](x: List[T])(implicit m: Manifest[T]) = {
  println(m)
  if (m <:< manifest[String])
    println("Hey, this list is full of strings")
  else
    println("Non-stringy list")
}

foo(List("one", "two")) // Hey, this list is full of strings
foo(List(1, 2)) // Non-stringy list
foo(List("one", 2)) // Non-stringy list


import scala.reflect._

def makePair[T : ClassTag](first: T, second: T) = {
    val r = new Array[T](2); r(0) = first; r(1) = second; r
}

val a = makePair(4, 2) // An Array[Int]
a.getClass // In the JVM, [I is an int[] array

val b = makePair("Fred", "Brooks") // An Array[String]
b.getClass // In the JVM, [Ljava.lang.String; is a String[] array