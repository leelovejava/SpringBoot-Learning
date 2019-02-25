// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// 传统定义两个参数
def mul(x:Int, y: Int) = x * y
mul(6,7)
// 柯里化定义，使用到了闭包
def mulOneAtATime(x: Int) = (y: Int) => x * y

mulOneAtATime(6)(7)

// Scala中可以简写
def mulOneAtATime(x: Int)(y: Int) = x * y

val a = Array("Hello", "World")
val b = Array("hello", "world")
// def corresponds[B](that: GenSeq[B])(p: (A,B) => Boolean): Boolean
a.corresponds(b)(_.equalsIgnoreCase(_))


