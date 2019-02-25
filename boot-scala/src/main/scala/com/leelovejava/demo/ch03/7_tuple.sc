// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// 元组操作
(1, 3.14, "Fred")

val t = (1, 3.14, "Fred")

val second = t._2

val first = t _1

//变量赋值
val (first1, second1, third) = t

val (first2, second2, _) = t


// 拉链操作
val symbols = Array("<", "-", ">")
val counts = Array(2, 10, 2)
val pairs = symbols.zip(counts)

for ((s, n) <- pairs) print(s * n)

symbols.zip(counts).toMap
