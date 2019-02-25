package com.leelovejava.demo.ch10.sec02

import java.io.Serializable

trait Logger {
  def log(msg: String) // An abstract method
}

class ConsoleLogger extends Logger // Use extends, not implements
  with Cloneable with Serializable { // Use with to add multiple traits
  def log(msg: String) { println(msg) } // No override needed
}

object Main extends App {
  val logger = new ConsoleLogger
  logger.log("Exiting Main")
}
