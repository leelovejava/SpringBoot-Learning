package com.leelovejava.demo.ch08.sec02

object Sec02Main extends App {
  val fred = new com.leelovejava.demo.ch08.sec02.Employee(50000)
  fred.giveRaise(10)
  println(fred + ": " + fred.description)

  val wilma = new com.leelovejava.demo.ch08.sec02.Manager
  wilma.subordinates += fred
  wilma.subordinates += new com.leelovejava.demo.ch08.sec02.Employee(50000)
  println(wilma + ": " + wilma.description)
}
    object Utils {
      def percentOf(value: Double, rate: Double) = value * rate / 100
    }

class Employee(initialSalary: Double) {
        private var salary = initialSalary
        def description = "An employee with salary " + salary
        def giveRaise(rate: scala.Double) {
          salary += Utils.percentOf(salary, rate)
          // Ok to access Utils from parent package
        }
      }
