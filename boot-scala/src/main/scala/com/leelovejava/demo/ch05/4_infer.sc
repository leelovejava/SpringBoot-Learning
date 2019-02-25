// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

def valueAtOneQuarter(f: (Double) => Double) = f(0.25)

// 传入函数表达式
valueAtOneQuarter((x: Double) => 3 * x)
// 参数推断省去类型信息
valueAtOneQuarter((x) => 3 * x)
//  单个参数可以省去括号
valueAtOneQuarter(x => 3 * x)
// 如果变量旨在=>右边只出现一次，可以用_来代替
valueAtOneQuarter(3 * _)

val fun2 = 3 * (_: Double) // OK
val fun3: (Double) => Double = 3 * _ // OK

val fun1 = 3 * _ // Error: Can’t infer types