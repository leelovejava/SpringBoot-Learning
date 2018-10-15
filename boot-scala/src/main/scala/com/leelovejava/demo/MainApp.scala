package com.leelovejava.demo

import java.io.File
import scala.io.Source

/**
  * 隐式转换
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