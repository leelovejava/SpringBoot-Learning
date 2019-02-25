package com {
  package sec01 {
    package impatient {
      class Employee(id: Int) {
        def description = "An employee with id " + id
      }      
    }
  }
}

package net {
  package bigjava {
    class Counter {
      private var value = 0 
      def increment() { value += 1 } 
      def description = "A counter with value " + value
    }
  }
}

object Sec01Main extends App {
  val fred = new com.sec01.impatient.Employee(1729)
  val wilma = new com.leelovejava.demo.ch08.sec01.Manager("Wilma")
  val myCounter = new net.bigjava.Counter
  println(fred + ": " + fred.description)
  println(wilma + ": " + wilma.description)
  println(myCounter + ": " + myCounter.description)
}
