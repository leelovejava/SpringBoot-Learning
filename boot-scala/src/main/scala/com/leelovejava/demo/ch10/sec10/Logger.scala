package com.leelovejava.demo.ch10.sec10

import java.io._
import java.util._

trait Logger {
  println("Constructing Logger")

  def log(msg: String)
}

trait FileLogger extends Logger {
  println("Constructing FileLogger")

  val out = new PrintWriter("app.log") // Part of the trait’s constructor
  out.println(s"# ${java.time.Instant.now()}") // Also part of the constructor

  def log(msg: String) { out.println(msg); out.flush() }
}

trait ShortLogger extends Logger {
  println("Constructing ShortLogger")

  val maxLength: Int // An abstract field
  abstract override def log(msg: String) {
    super.log(
      if (msg.length <= maxLength) msg
      else s"${msg.substring(0, maxLength - 3)}...")
  }
}

class Account {
  println("Constructing Account")

  protected var balance = 0.0
}

class SavingsAccount extends Account with FileLogger with ShortLogger {
  println("Constructing SavingsAccount")

  val maxLength = 15
  def withdraw(amount: Double) {
    if (amount > balance) log("Insufficient funds")
    else balance -= amount
  }

  // More methods ...
}

object Main extends App {
  val acct = new SavingsAccount
  acct.withdraw(100) 
  println("Look into app.log for the log message.");
}
