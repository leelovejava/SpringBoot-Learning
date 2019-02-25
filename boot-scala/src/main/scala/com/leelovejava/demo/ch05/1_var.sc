// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

import scala.math._
val num = 3.14
// _将ceil方法转换成函数，将函数赋值给变量
val fun = ceil _

fun(num)

// 传递函数
Array(3.14, 1.42, 2.0).map(fun)


def plus(x: Double) = 3 * x


Array(3.14, 1.42, 2.0).map(plus)

