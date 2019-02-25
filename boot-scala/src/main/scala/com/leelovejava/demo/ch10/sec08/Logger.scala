package com.leelovejava.demo.ch10.sec08

trait Logger {
  def log(msg: String)
}

trait ConsoleLogger extends Logger {
  def log(msg: String) { println(msg) }
}

trait ShortLogger extends Logger {
  val maxLength = 15 
  abstract override def log(msg: String) {
    super.log(
      if (msg.length <= maxLength) msg
      else s"${msg.substring(0, maxLength - 3)}...")
  }
}

class Account {
  protected var balance = 0.0
}

// 只要有一个具体方法即可
class SavingsAccount extends Account with ConsoleLogger with ShortLogger {
  var interest = 0.0
  def withdraw(amount: Double) {
    if (amount > balance) log("Insufficient funds")
    else balance -= amount
  }

  // More methods ...
}

object Main extends App {
  val acct = new SavingsAccount
  acct.withdraw(100)
  acct.maxLength
}

/*

$ javap -private Account.class 
Compiled from "Logger.scala"
public class Account {
  private double balance;
  ...
}

$ javap -private SavingsAccount.class 
Compiled from "Logger.scala"
public class SavingsAccount extends Account implements ConsoleLogger,ShortLogger {
  private double interest;
  private final int maxLength;
  ...
}

*/
