package com.leelovejava.demo.ch09.sec09

// Compile without, then with -Xcheckinit

class Creature {
  val range: Int = 10
  val env: Array[Int] = new Array[Int](range)
}

class Ant extends Creature {
  override val range = 2
}

// 提前定义
class Bug extends {
  override val range = 3
} with Creature

class Creature1 {
  lazy val range: Int = 10
  val env: Array[Int] = new Array[Int](range)
}

class Cow extends Creature1 {
  override lazy val range = 4
}

object Main extends App {
  val a = new Ant
  println(a.range)
  println(a.env.length)

  val b = new Bug
  println(b.range)
  println(b.env.length)
}
