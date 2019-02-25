
// Blank line above on purpose
// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// 切片操作返回一个迭代器
val iter1 = (1 to 10).sliding(3)

while (iter1.hasNext)
  println(iter1.next())


val iter2 = (1 to 10).sliding(3)

for (elem <- iter2)
  println(elem)

val iter3 = (1 to 10).sliding(3)

// 执行length操作，会消费迭代器
println(iter3.length)

println(iter3.hasNext) // The iterator is now consumed

val iter4 = (1 to 10).sliding(3)

// 返回迭代器数组
iter4.toArray