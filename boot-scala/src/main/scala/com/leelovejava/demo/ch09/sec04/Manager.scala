package com.leelovejava.demo.ch09.sec04

class Person {
  protected[this] var age = 0
  def setAge(newAge: Int) { // A person can never get younger
    if (newAge > age) age = newAge 
  }    
}

class Manager extends Person {
  protected var salary = 0.0
  def setSalary(newSalary: Double) { // A manager's salary can never decrease
    if (newSalary > salary) salary = newSalary 
  } 
  // Can access age from superclass
  def description = "a manager who is " + age +
    " years old and makes " + salary

  def isSeniorTo(other: Manager) =
    salary > other.salary 
    // Can't access age of another person. The following doesn't work:
    // age > other.age
}

object Main extends App {
  var fred = new Manager
  fred.setAge(50)
  fred.setSalary(100000)
  var wilma = new Manager
  wilma.setAge(55)
  wilma.setSalary(90000)
  if (fred.isSeniorTo(wilma)) 
    println(fred.description + "\nis senior to " + wilma.description)
  else
    println(wilma.description + "\nis senior to " + fred.description)
}

