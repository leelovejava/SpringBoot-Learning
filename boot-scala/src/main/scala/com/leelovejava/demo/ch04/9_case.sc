//package com.atguigu.scala.ch11.sec09

abstract class Amount
case class Dollar(value: Double) extends Amount
case class Currency(value: Double, unit: String) extends Amount

case object Nothing extends Amount

val dollar = Dollar(1000.0)

dollar.value

dollar.toString

for (amt <- Array(Dollar(1000.0), Currency(1000.0, "EUR"), Nothing)) {
  val result = amt match {
    case Dollar(v) => "$" + v
    case Currency(_, u) => "Oh noes, I got " + u
    case Nothing => ""
  }
  // Note that amt is printed nicely, thanks to the generated toString
  println(amt + ": " + result)
}

