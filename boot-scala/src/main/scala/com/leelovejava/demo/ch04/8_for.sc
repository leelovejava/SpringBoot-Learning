// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

import scala.collection.JavaConverters._
// Converts Java Properties to a Scala map—just to get an interesting example
for ((k, v) <- System.getProperties.asScala)
  println(k + " -> " + v)

// 忽略匹配失败的项目，打出所有value为空的条目
for ((k, "") <- System.getProperties.asScala)
  println(k)

// 通过守卫提取所有V等于空的属性。
for ((k, v) <- System.getProperties.asScala if v == "") 
  println(k)
