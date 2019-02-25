// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

val str = "+-3!"
// 返回str的一组索引Range
for (i <- str.indices) {
  var sign = 0
  var digit = 0

  str(i) match {
    case '+' => sign = 1
    case '-' => sign = -1
    case ch if Character.isDigit(ch) => digit = Character.digit(ch, 10)
    case _ => 
  }

  println(str(i) + " " + sign + " " + digit)
}

import scala.math._
val x = random
x match {
  case Pi => "It's Pi"
  case _ => "It's not Pi"
}
// 变量必须以小写字母开头，常量用大写字母，如果常量用小写字母开头需要加反引号。
import java.io.File._
str match {
  case `pathSeparator` => "It's the path separator"
  case _ => "It's not the path separator"
}

