// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.


// 和Java的互操作

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

val command = ArrayBuffer("ls", "-al", "/")
// ProessBuilder是java方法
val pb = new ProcessBuilder(command.asJava) // Scala to Java

val cmd : mutable.Buffer[String] = pb.command().asScala // Java to Scala

cmd == command
