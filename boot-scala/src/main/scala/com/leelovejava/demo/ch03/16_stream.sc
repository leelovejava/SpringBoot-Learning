
// #:: 返回一个流
def numsForm(n: BigInt) : Stream[BigInt] = n #:: numsForm( n + 1 )

val tenOrMore = numsForm(10)

tenOrMore.tail

tenOrMore.head

// 获取最后一个元素
tenOrMore.tail.tail.tail


var squares = numsForm(5).map(x => x * x)

squares.take(5).force

//squares.force   不要尝试对一个无穷流的成员进行求值，OutOfMemoryError