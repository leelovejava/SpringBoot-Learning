package com.leelovejava.demo.ch10.sec03

trait ConsoleLogger {
  def log(msg: String) { println(msg) }
}

class Account {
  protected var balance = 0.0
}

class SavingsAccount extends Account with ConsoleLogger {
  def withdraw(amount: Double) {
    if (amount > balance) log("Insufficient funds")
    else balance -= amount
  }
  // More methods ...
}

object Main extends App {
  val acct = new SavingsAccount
  acct.withdraw(100)
}
