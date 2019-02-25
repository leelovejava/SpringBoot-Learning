package com.leelovejava.demo.ch10.sec14

import java.io._

trait Logger {
  def log(msg: String)
}

trait ConsoleLogger extends Logger {
  def log(msg: String) { println(msg) }
}

trait ShortLogger extends Logger {
  val maxLength = 15 // A concrete field
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

class SavingsAccount extends Account with ConsoleLogger with ShortLogger {
  def withdraw(amount: Double) {
    if (amount > balance) log("Insufficient funds")
    else balance -= amount
  }

  // More methods ...
}

trait LoggedException extends Exception with ConsoleLogger {
  def log() { log(getMessage()) }
}

class UnhappyException extends IllegalStateException 
  with LoggedException { // This class extends a trait
  override def getMessage() = "arggh!"
}

/*

$ javap -private Logger
Compiled from "Logger.scala"
public interface Logger {
  public abstract void log(java.lang.String);
}

$ javap -private ConsoleLogger
Compiled from "Logger.scala"
public interface ConsoleLogger extends Logger {
  public void log(java.lang.String);
  public void $init$();
}

$ javap -private ShortLogger
Compiled from "Logger.scala"
public interface ShortLogger extends Logger {
  public abstract void ShortLogger$_setter_$maxLength_$eq(int);
  public abstract void ShortLogger$$super$log(java.lang.String);
  public int maxLength();
  public void log(java.lang.String);
  public void $init$();
}

$ javap -private SavingsAccount
Compiled from "Logger.scala"
public class SavingsAccount extends Account implements ConsoleLogger,ShortLogger {
  private final int maxLength;
  public int maxLength();
  public void ShortLogger$$super$log(java.lang.String);
  public void ShortLogger$_setter_$maxLength_$eq(int);
  public void log(java.lang.String);
  public void withdraw(double);
  public SavingsAccount();
}

$ javap LoggedException.class 
Compiled from "Logger.scala"
public interface LoggedException extends ConsoleLogger {
  public void log();
  public void $init$();
}

$ javap UnhappyException.class 
Compiled from "Logger.scala"
public class UnhappyException extends java.lang.IllegalStateException implements LoggedException,ConsoleLogger {
  public void log();
  public void log(java.lang.String);
  public java.lang.String getMessage();
  public UnhappyException();
}

*/
