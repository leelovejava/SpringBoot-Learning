// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

val digits = List(4, 2)

// 右结合操作符
9 :: List(4, 2)

// Nil表示空列表
9 :: 4 :: 2 :: Nil

9 :: (4 :: (2 :: Nil))

def sum(lst: List[Int]): Int = if (lst == Nil) 0 else lst.head + sum(lst.tail)

sum(List(9, 4, 2))

def sum(lst: List[Int]): Int = lst match {
  case Nil => 0
  case h :: t => h + sum(t) // h is lst.head, t is lst.tail
}

sum(List(9, 4, 2))

List(9, 4, 2).sum
