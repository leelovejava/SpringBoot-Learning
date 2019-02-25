val q1 = new scala.collection.mutable.Queue[Int]

//追加元素
q1 += 1

q1 += 2

//追加多个元素并返回队列
q1 ++= List(3, 4)

//返回并从队列删除第一个元素
q1.dequeue()

//追加多个元素，返回类型为Unit
q1.enqueue(5, 6, 7)

q1

//队列首部
q1.head

//队列尾部
q1.tail
