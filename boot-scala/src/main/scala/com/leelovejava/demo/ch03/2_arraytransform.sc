
val a = Array(2, 3, 5, 7, 11)

// 产生新的数组
val result = for (elem <- a) yield 2 * elem


for (elem <- a if elem % 2 == 0) yield 2 * elem


a.filter(_ % 2 == 0).map(2 * _)


// 常用方法
import scala.collection.mutable.ArrayBuffer

// 求和
Array(1, 7, 2, 9).sum

//最大排序
ArrayBuffer("Mary", "had", "a", "little", "lamb").max

val b = ArrayBuffer(1, 7, 2, 9)

//排序
val bSorted = b.sorted

val a1 = Array(1, 7, 2, 9)

scala.util.Sorting.quickSort(a1)

a1

// 形成String
a1.mkString(" and ")

a1.mkString("<", ",", ">")

a1.toString

b.toString

import scala.collection.mutable.ArrayBuffer

// 更多方法
val a2 = Array(1, -2, 3, -4, 5)
val b2 = ArrayBuffer(1, 7, 2, 9)

// 统计大于0的个数
a2.count(_ > 0)

//
b2.append(1, 7, 2, 8)

b2

b2.appendAll(a2)

b2

b2 += 4 -= 7

b2

//将b2中的数据copy到a2中，a2的空间为5
b2.copyToArray(a2)
a2

val xs = Array(1, "Fred", 2, 9)
b2.copyToArray(xs)
xs

b2
// 加到20项，用-1填充
b2.padTo(20, -1)

(1 to 10).padTo(20, -1) // Note that the result is a Vector, not a Range