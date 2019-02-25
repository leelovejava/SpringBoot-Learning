package com.leelovejava.demo.ch10.sec05

trait Logger {
  def log(msg: String);
}
// 继承Logger特质，提供具体的log方法
trait ConsoleLogger extends Logger {
  def log(msg: String) { println(msg) }
}

// 注意super
trait TimestampLogger extends ConsoleLogger {
  override def log(msg: String) {
    super.log(new java.util.Date() + " " + msg)
  }
}

trait ShortLogger extends ConsoleLogger {
  override def log(msg: String) {
    super.log(
      if (msg.length <= 15) msg else s"${msg.substring(0, 12)}...")
  }
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
  val acct1 = new SavingsAccount with TimestampLogger with ShortLogger
  val acct2 = new SavingsAccount with ShortLogger with TimestampLogger
  acct1.withdraw(100) 
  acct2.withdraw(100)
}

