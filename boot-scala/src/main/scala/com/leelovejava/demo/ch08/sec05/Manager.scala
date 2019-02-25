package com.leelovejava.demo.ch08.sec05

import java.awt._


class Manager {
  import collection.mutable._
  val subordinates = new ArrayBuffer[Employee]
  def description = "A manager with " + subordinates.length + " subordinates" + Font.BOLD
}

class Employee(val name: String)

// Run as scala com.horstmann.impatient.Main

object Sec05Main extends App {
  val wilma = new Manager
  val employees = collection.mutable.HashSet( 
    // import collection.mutable._ doesn't extend until here
    new Employee("Fred"), new Employee("Barney"))
  wilma.subordinates ++= employees
  println(wilma + ": " + wilma.description)
}

