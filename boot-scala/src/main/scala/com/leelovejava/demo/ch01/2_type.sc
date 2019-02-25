// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.sc to run them all at once.

// 隐式转换成RichInt
1.toString()

1.to(10)

// String 隐式转换成 StringOps
"Hello".intersect("World") 

// 类型之间的转换需要通过方法进行，没有强制转化
99.04.toInt

val i = null

val j:Int = null