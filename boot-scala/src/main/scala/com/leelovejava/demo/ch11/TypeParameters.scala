package com.leelovejava.demo.ch11

// 类型参数
   //类和特质都可以带类型参数，用方括号来定义类型参数，可以用类型参数来定义变量、方法参数和返回值。
   //函数和方法也可以有类型参数

package object para{

  def printPair[T,S](t:(T,S)) = println(t._1.getClass +" --- "+t._2.getClass)

  def makeFriends(p: Pair7[Person]) = p.first + " and " + p.second + " are now friends."
}

//****************** 对偶 *****************
  case class Pair(val first: String, val second: String){
    def makeTuple = (first, second)
    override def toString: String = "(" + first + "," + second + ")"
  }

  //****************** 泛型对偶 *************
  case class Pair2[T](val first: T, val second: T) {
    def makeTuple = (first, second)
    override def toString = "(" + first + "," + second + ")"
  }

  //****************** 泛型对偶【类型变量上限】 *************
  case class Pair3[T <: Comparable[T]](val first: T, val second: T) {
    //希望传入的类型能够用compareTo比较大小
    def smaller = if (first.compareTo(second) < 0) first else second
    def mkdirTuple = (first, second)
    override def toString = "(" + first + "," + second + ")"
  }

  //****************** 泛型对偶【视图界定】 *************
  case class Pair4[T <% Comparable[T]](val first: T, val second: T) {
    //希望传入的类型能够用compareTo比较大小， RichInt实现了 需要一个隐式转换，让Int可以转换为RichInt
    def smaller = if (first.compareTo(second) < 0) first else second
    def mkdirTuple = (first, second)
    override def toString = "(" + first + "," + second + ")"
  }

  //****************** 泛型对偶【类型变量下限】 *************
  class Person(name: String)
  class Student(name: String) extends Person(name)

  case class Pair5[T](val first: T, val second: T) {
    def replaceFirst[R >: T](newFirst: R) = new Pair5(newFirst, second)
    override def toString = "(" + first + "," + second + ")"
  }

  //****************** 泛型对偶【上下文界定】 *************
  case class Pair6[T : Ordering](val first: T, val second: T) {
    // 希望传入一个存在一个类型为Ordering[T]的隐式值的可比较类型参数
    def smaller(implicit ord: Ordering[T]) ={
      println(ord)
      if (ord.compare(first, second) < 0) first else second
    }
  }

  //****************** 泛型对偶【协变】 *************
  //用自己替换需要自己父亲的位置是允许的
  case class Pair7[+T](val first: T, val second: T)




  object Main3 extends App{
    //生成String,Int类型的Tuple
    //println(Pair("China","960").makeTuple)

    //生成任意类型对偶
    //println(Pair2[Double](960.05,960.05).makeTuple)
    //println(Pair2(960,960).makeTuple)

    //printPair[Int,Int](Pair2[Int](960,960).makeTuple)
    //printPair(Pair2[Double](960.05,960.05).mkdirTuple)

    //打印对比结果
    //print(Pair3("abc","def").smaller)

    //println(Pair3("960.05","560.05").smaller)

    //打印两个数字大小比较
    //println(Pair4(50,20).smaller)

    //学生组队
    //val studentPair = Pair5(new Student("Fred"),new Student("Wilma"))
    //studentPair.replaceFirst(new Person("Barney"))

    // 上下文界定
    //println(Pair6("china","usa").smaller)

    // 协变 希望子类也能成为朋友
    println(para.makeFriends( Pair7(new Person("Barney"),new Person("Lucy")) ))
    println(para.makeFriends( Pair7(new Student("Fred"),new Student("Wilma")) ))


  }
