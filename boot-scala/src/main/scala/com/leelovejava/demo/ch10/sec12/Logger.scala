package com.leelovejava.demo.ch10.sec12

trait Logger {
  def log(msg: String)
}

trait ConsoleLogger extends Logger {
  def log(msg: String) { println(msg) }
}

trait LoggedException extends Exception with ConsoleLogger {
  def log() { log(getMessage()) }
}

class UnhappyException extends IllegalStateException 
  with LoggedException { // This class extends a trait
  override def getMessage() = "arggh!"
}

class Account {
  protected var balance = 0.0
}

class SavingsAccount extends Account {
  def withdraw(amount: Double) {
    if (amount > balance) throw new UnhappyException with ConsoleLogger
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
