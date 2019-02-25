// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// Scala风格
"Hello" intersect "World" // Can omit . and () for binary method
"Hello".intersect("World")

// 没有++、--操作符
var counter = 0
counter += 1 // Increments counter—Scala has no ++

// 提供BigInt和BigDecimal对象来处理大数据
val x: BigInt = 1234567890
x * x * x

