package com.leelovejava.demo.ch10.sec13

trait Logger {
  def log(msg: String)
}

trait ConsoleLogger extends Logger {
  def log(msg: String) { println(msg) }
}

trait LoggedException extends ConsoleLogger {
  this: Exception => // or this: { def getMessage() : String } =>
    def log() { log(getMessage()) }
}

class Account {
  protected var balance = 0.0
}

class SavingsAccount extends Account {
  def withdraw(amount: Double) {
    if (amount > balance) throw 
      new IllegalStateException("Insufficient funds") 
        with LoggedException with ConsoleLogger
    else balance -= amount
  }

  // More methods ...
}

object Main extends App {
  try {
    val acct = new SavingsAccount
    acct.withdraw(100)
  } catch {
    case e: LoggedException => e.log()
  }
}
