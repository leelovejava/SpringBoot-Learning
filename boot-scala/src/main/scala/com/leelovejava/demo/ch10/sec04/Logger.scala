package com.leelovejava.demo.ch10.sec04

trait Logger {
  def log(msg: String);
}
// 继承Logger
trait ConsoleLogger extends Logger {
  def log(msg: String) { println(msg) }
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
  val acct = new SavingsAccount with ConsoleLogger
  acct.withdraw(100)
}

