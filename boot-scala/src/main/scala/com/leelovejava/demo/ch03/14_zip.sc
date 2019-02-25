// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

val prices = List(5.0, 20.0, 9.95)
val quantities = List(10, 2, 1)

prices zip quantities

// 拉链后执行map操作
(prices zip quantities) map { p => p._1 * p._2 }

((prices zip quantities) map { p => p._1 * p._2 }) sum

List(5.0, 20.0, 9.95) zip List(10, 2)

// 如果一个元素数量比较少，则用默认值代替
List(5.0, 20.0, 9.95).zipAll(List(10, 2), 0.0, 1)

List(5.0).zipAll(List(10, 2), 0.0, 1)

// 和索引值序列进行拉链操作
"Scala".zipWithIndex

"Scala".zipWithIndex.max

"Scala".zipWithIndex.max._2


//unzip函数可以将一个元组的列表转变成一个列表的元组
"Scala".zipWithIndex.unzip

"Scala".zipWithIndex.unzip._1

"Scala".zipWithIndex.unzip._2