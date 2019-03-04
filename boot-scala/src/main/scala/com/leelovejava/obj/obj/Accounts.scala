package com.leelovejava.obj.obj

/**
  * Scala对象之单例对象
  */
object Accounts {
  /**
    * 单例对象
    * Scala中没有静态方法和静态字段,可以用object语法达到同样目的
    */
  println("init")
  private var lastNumber = 0

  /**
    * 对象具有类的所有特性,但不能给构造器设置参数
    * @return
    */
  def newUniqueNumber() = {
    lastNumber += 1
    // 最后一行为返回
    lastNumber
  }
  // 调用 Accounts.newUniqueNumber()
}
