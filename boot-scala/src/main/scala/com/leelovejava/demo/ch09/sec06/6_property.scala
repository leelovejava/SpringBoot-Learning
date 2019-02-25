// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.
package com.leelovejava.demo.ch09.sec06

// It is more common to override an abstract def with a val

abstract class Person { // See Section 8.8 for abstract classes
  def id: Int // Each person has an ID that is computed in some way  
}

class Student(override val id: Int) extends Person

class SecretAgent extends Person {
  override val id = scala.util.Random.nextInt
}


object Main extends App {
  val fred = new Student(1729)
  fred.id
  val james = new SecretAgent
  james.id
}