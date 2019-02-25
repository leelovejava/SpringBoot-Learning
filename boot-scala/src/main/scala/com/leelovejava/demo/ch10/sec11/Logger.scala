package com.leelovejava.demo.ch10.sec11

import java.io._
import java.util._

trait Logger {
  def log(msg: String)
}

// Use early definition syntax to define filename
trait FileLogger extends Logger {
  val filename: String
  val out = new PrintWriter(filename) 
  def log(msg: String) { out.println(msg); out.flush() }
}

// This version constructs the writer lazily
trait FileLogger2 extends Logger {
  val filename: String
  lazy val out = new PrintWriter(filename) 
  def log(msg: String) { out.println(msg); out.flush() }
}

class Account {
  protected var balance = 0.0
}

abstract class SavingsAccount extends Account with Logger {
  def withdraw(amount: Double) {
    if (amount > balance) log("Insufficient funds")
    else balance -= amount
  }

  // More methods ...
}

object Main extends App {
  val acct = new {
    val filename = "myapp.log"
  } with SavingsAccount with FileLogger
  acct.withdraw(100) 

  val acct2 = new SavingsAccount with FileLogger2 {
    val filename = "myapp2.log"
  } 
  acct2.withdraw(100)   
  println("Look into myapp.log and myapp2.log for the log messages.");
}

