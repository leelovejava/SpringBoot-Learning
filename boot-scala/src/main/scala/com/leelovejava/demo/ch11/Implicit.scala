package com.leelovejava.demo.ch11

// 隐式转换和隐式参数
   // 隐式转换函数是以implicit关键字声明的带有单个参数的函数。这种函数将会自动应用，将值从一种类型转换为另一种类型。

  //Scala会考虑如下的隐式转换函数：
  //1)	位于源或目标类型的伴生对象中的隐式函数。
  //2)	位于当前作用域可以以单个标识符指代的隐式函数。

  //隐式转换一般发生在三种情况下：
  //1)	当表达式的类型和预期类型不一致的时候。
  //2)	当对象访问一个不存在的成员时。
  //3)	当对象调用某个方法，而该方法的参数声明和传入参数不匹配时。

  //隐式参数一般从两个地方进行查找：
  //1)	在当前作用域所有可以用单个标识符指代的满足类型要求的val和def
  //2)	该值需要声明为implicit


//******************  定制化美化符  ****************
//case class Delimiters(left: String, right: String)

//******************  美化分数类  ******************
/*class BeautifyFraction(fraction: Fraction){
  def beautify = "<< " + fraction + " >>"
}*/

case class Delimiteres(left:String,right:String)

class BeautifyFraction(fraction: Fraction)(implicit delimiteres: Delimiteres){
  def beautify = delimiteres.left + " "+fraction +" "+delimiteres.right
}
object BeautifyFraction{

}

//import BeautifyFraction._

//class BeautifyFraction(fraction: Fraction)(implicit delims: Delimiters){
//  def beautify = delims.left + " " + fraction + " " + delims.right
//}
//object BeautifyFraction {
//  implicit val quoteDelimiters = Delimiters("«=", "=»")
//}
//import BeautifyFraction._

//******************  分数类  *********************
class Fraction(val num: Int, val den: Int) {
  // 定义值的输出
  override def toString = num + "/" + den
  // 定义乘法操作
  def *(other: Fraction) = new Fraction(num * other.num, den * other.den)
}
object Fraction {
  // 定义apply，用于对象生成
  def apply(num: Int, den: Int) = new Fraction(num, den)
  // 定义隐式转换函数（a2b这种形式）
  //implicit def int2Fraction(n: Int) = Fraction(n, 1)
  implicit def int2Fraction(n:Int) = Fraction(n,1)
  // 定义美化隐式转换函数
  //implicit def fraction2BeautifyFraction(fraction: Fraction) = new BeautifyFraction(fraction)
}

//******************  Main  *********************
object Main2 extends App {
  //implicit def fraction2BeautifyFraction(fraction: Fraction) = new BeautifyFraction(fraction)
  //implicit val delimiteres = new Delimiteres("<--","-->")
  //定义两个分数相乘
  println(Fraction(4,5))
  println(Fraction(4,5) * Fraction(3,5))

  println(3 * Fraction(4,5))
  //如果想用数字直接相乘呢？
  //println(3 * Fraction(4, 5))
  //println(Fraction(4,5).beautify)

  //需要美化输出
  //println(Fraction(3,5).beautify)

}

