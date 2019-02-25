package com.leelovejava.demo.train

/**
 * trait 特质特性，第一个关键字用extends，之后用with
 */

trait Speak{
  def speak(name:String) = {
    println(name+" is speaking...")
  }
}
trait Listen{
  def listen(name:String) = {
    println(name+" is listening...")
  }
}

class Person1() extends Speak with Listen{
  
}


object Lesson_Trait01 {
  def main(args: Array[String]): Unit = {
    val p = new Person1()
    p.speak("zhangsan")
    p.listen("lisi")
  }
}