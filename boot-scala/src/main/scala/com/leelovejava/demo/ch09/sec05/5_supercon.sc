// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

package com.atguigu.scala.ch09.sec05

class Person(val name: String, val age: Int) {
  override def toString = getClass.getName + "[name=" + name +
    ",age=" + age + "]"
}

class Employee(name: String, age: Int, val salary : Double) extends
  Person(name, age) {
  override def toString = super.toString + "[salary=" + salary + "]"
}



object Main extends App {
  new Employee("Fred", 42, 50000)
}