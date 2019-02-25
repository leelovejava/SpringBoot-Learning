// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// 函数定义，返回类型可以自行推断
def abs(x: Double) = if (x >= 0) x else -x

println(abs(3))
println(abs(-3))

def fac(n : Int) = {
  var r = 1
  for (i <- 1 to n) r = r * i
  r   // {} 最后一个表达式为返回值
}

println(fac(10))

// 递归函数 必须指定返回值类型
def recursiveFac(n: Int): Int = 
  if (n <= 0) 1 else n * recursiveFac(n - 1)

println(recursiveFac(10))

// 设置默认参数
def decorate(str: String, left: String = "[", right: String = "]") =
  left + str + right

println(decorate("Hello"))

println(decorate("Hello", "<<<", ">>>"))

println(decorate("Hello", ">>>["))

println(decorate(left = "<<<", str = "Hello", right = ">>>"))

println(decorate("Hello", right = "]<<<"))

// 变长参数
def sum(args: Int*) = {
  var result = 0
  for (arg <- args) result += arg
  result
}

val s = sum(1, 4, 9, 16, 25)
println(s)

// _* 告诉编译器将1 to 5 当做参数序列处理
val s2 = sum(1 to 5: _*)
println(s2)

// head是第一个元素，tail是剩下的元素的集合
def recursiveSum(args: Int*) : Int = {
  if (args.length == 0) 0
  else args.head + recursiveSum(args.tail : _*)
}

println(recursiveSum(1, 4, 9, 16, 25))


// 过程定义
def box(s : String) { // Look carefully: no =
  val border = "-" * s.length + "--\n"
  println("\n" + border + "|" + s + "|\n" + border)
}

box("Fred")

box("Wilma")

// 或者如此定义
def box1(s : String) : Unit = { // Look carefully: no =
  val border = "-" * s.length + "--\n"
  println("\n" + border + "|" + s + "|\n" + border)
}

box1("Fred")

box1("Wilma")

