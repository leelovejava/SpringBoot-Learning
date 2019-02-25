// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

for (obj <- Array(42, "42", BigInt(42), BigInt, 42.0)) {

  val result = obj match {
    case x: Int => x
    case s: String => s.toInt
    case _: BigInt => Int.MaxValue
    case BigInt => -1
    case _ => 0
  }

  println(result)
}

// Map(42 -> "Fred")也映射到Map[String, Int]，显然不对，运行期已经没有类型信息
for (obj <- Array(Map("Fred" -> 42), Map(42 -> "Fred"), Array(42), Array("Fred"))) {

  val result = obj match {
    case m: Map[String, Int] => "It's a Map[String, Int]"
    // Warning: Won't work because of type erasure
    case m: Map[_, _] => "It's a map"
    case a: Array[Int] => "It's an Array[Int]"
    case a: Array[_] => "It's an array of something other than Int"
  }

  println(result)
}
