// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.
package com.leelovejava.demo.ch09.sec08

abstract class Person(val pname: String) {
  val id: Int
  // No initializer—this is an abstract field with an abstract getter method
  var name: String
  // Another abstract field, with abstract getter and setter methods
  def idString: Int // No method body—this is an abstract method
}

class Employee(pname: String) extends Person(pname) {
  val id = 5;
  var name = ">>>"
  def idString = pname.hashCode // override keyword not required
}



object Main extends App {
  val fred = new Employee("Fred")
  fred.id
  fred.name
  fred.idString
}