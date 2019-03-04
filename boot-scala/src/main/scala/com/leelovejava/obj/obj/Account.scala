package com.leelovejava.obj.obj

/**
  * Scala对象之伴生对象
  */
class Account {
  var id = Accounts.newUniqueNumber()
  private var balance = 0.0

  def deposit(amount: Double): Unit = {
    balance += amount
  }

  def apply: Unit ={
    println("class Account apply")
  }
}

/**
  * object Account是class Account的伴生对象
  */
object Account {
  private var lastNumber = 0

  private def newUniqueNumber() = {
    lastNumber += 1
    lastNumber
  }

  /**
    * apply方法
    * def apply: Account = new Account()
    * @param initialBance
    * @return
    */
  /*def apply(initialBance: Double): Unit = {
    println("object Account apply")
    new Account(newUniqueNumber(), initialBance)
  }*/
}

