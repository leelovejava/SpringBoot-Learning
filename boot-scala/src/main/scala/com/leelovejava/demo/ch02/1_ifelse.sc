// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

val x = 0

// if else 语句结构
if (x > 0) 1 else -1

// 可以有返回值
val s = if (x > 0) 1 else -1

// 类型不一样时，返回公共超类型Any
if (x > 0) "positive" else -1

// 如果没有值，则返回Unit，类似于Java 的Void
if (x > 0) 1

if (x > 0) 1 else ()

//  需不需要分号

var n = 10
var r = 1
// 如果多行表达式卸载一行需要分号，单个表达式可以不用大括号
if (n > 0) { r = r * n; n -= 1 }

var s1, s0, v, v0, t, a, a0= 0.0

s1 = s0 + (v - v0) * t + // The + tells the parser that this is not the end
  0.5 * (a - a0) * t * t

// 不需要分号,{}的值就是最后一个表达式的值
if (n > 0) {
  r = r * n
  n -= 1
}