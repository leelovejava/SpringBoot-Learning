package com.leelovejava.demo.ch12

import java.io.File
import scala.io.Source
import scala.language.implicitConversions

object Main2 extends App {
  class RichFile(val from: File) {
    def read = Source.fromFile(from.getPath).mkString
  }

  implicit def file2RichFile(from: File) = new RichFile(from)

  val contents = new File("RichFile.scala").read
  println(contents)
}

/*
object Main extends App {
  implicit class RichFile(val from: File) extends AnyVal {
    def read = Source.fromFile(from.getPath).mkString
  }

  val contents = new File("RichFile2.scala").read
  println(contents)
}*/
