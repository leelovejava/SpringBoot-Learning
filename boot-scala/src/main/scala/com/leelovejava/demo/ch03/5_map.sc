// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// 不可变构造映射
val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
// 可变映射
val scores1 = scala.collection.mutable.Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
// 空映射
val scores2 = new scala.collection.mutable.HashMap[String, Int]
// 对偶
"Alice" -> 10
// 对偶元组
val scores3 = Map(("Alice", 10), ("Bob", 3), ("Cindy", 8))

// 获取值
val bobsScore = scores("Bob")

// contains检测
val fredsScore = if (scores.contains("Fred")) scores("Fred") else 0

// 可以用getOrElse替换检测
scores.getOrElse("Bob", 0)

// 显示调用get，获得Option   Some  None
scores.get("Bob")
scores.get("Bob").get
scores.get("Fred")


// 设置值
scores1("Bob")= 11
scores1("Fred2")=7
scores1


scores1 += ("Bob" -> 10, "Fred" -> 7)

scores1 -= "Alice"

// 不可变映射不能修改，但能够产生新映射
val newScores = scores + ("Bob" -> 10, "Fred" -> 7) // New map with update

// 遍历
for ((k, v) <- scores) println(k + " is mapped to " + v)

// Key的Set集合
scores.keySet

// 遍历value
for (v <- scores.values) println(v)

// 产生新的Map
for ((k, v) <- scores) yield (v, k)

// 排序
val scores4 = scala.collection.immutable.SortedMap("Alice" -> 10,
  "Fred" -> 7, "Bob" -> 3, "Cindy" -> 8)

// 双向链表HashMap
val months = scala.collection.mutable.LinkedHashMap("January" -> 1,
  "February" -> 2, "March" -> 3, "April" -> 4, "May" -> 5,
  "June" -> 6, "July" -> 7, "August" -> 8, "September" -> 9,
  "October" -> 10, "November" -> 11, "December" -> 12)