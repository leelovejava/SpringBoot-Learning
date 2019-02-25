// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// 多个变量生成器
for (i <- 1 to 3; j <- 1 to 3) print((10 * i + j) + " ")

// 守卫模式
for (i <- 1 to 3; j <- 1 to 3 if i != j) print((10 * i + j) + " ")

// 多个守卫模式
for (i <- 1 to 3; j <- 1 to 3 if i != j if j < 2) print((10 * i + j) + " ")

// 引入变量
for (i <- 1 to 3; from = 4 - i; j <- from to 3) print((10 * i + j) + " ")

// 使用变量
for (i <- 1 to 3; from = 4 - i) print(from + " ")

// () 变成{}  都可以
for { i <- 1 to 3
     from = 4 - i
     j <- from to 3 }
  print((10 * i + j) + " ")

// yield 将输出到集合，这种类型叫for推导式
val result = for (i <- 1 to 10) yield i % 3

result

// for推导式返回的类型和第一个生成器的类型是兼容的
for (c <- "Hello"; i <- 0 to 1) yield (c + i).toChar

for (i <- 0 to 1; c <- "Hello") yield (c + i).toChar

