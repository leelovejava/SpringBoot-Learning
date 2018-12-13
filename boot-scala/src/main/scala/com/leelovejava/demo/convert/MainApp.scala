package com.leelovejava.demo.convert

import java.io.File

import scala.io.Source

/**
  * 隐式转换
  * 隐式转换相比较于隐式参数，使用起来更来灵活。如果 Scala 在编译时发现了错误，在报错之前，会先对错误代码应用隐式转换规则，如果在应用规则之后可以使得其通过编译，则表示成功地完成了一次隐式转换
  * @param from
  */
class RichFile(val from: File) {
  def read = Source.fromFile(from.getPath).mkString
}

object RichFile {
  //隐式转换方法
  implicit def file2RichFile(from: File) = new RichFile(from)

}

object MainApp{
  def main(args: Array[String]): Unit = {
    //导入隐式转换
    //import RichFile._
    import RichFile.file2RichFile
    println(new File("D://words.txt").read)

  }
}