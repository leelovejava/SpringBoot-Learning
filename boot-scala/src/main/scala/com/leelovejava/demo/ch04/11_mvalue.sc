// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 11_mvalue.sc to run them all at once.

case class Currency(value: Double, unit: String)

val amt = Currency(1000.0, "EUR")

// 中置表达式
amt match { case a Currency u => a + " " + u }

val lst = List(1, 7, 2, 9)
lst match { 
  case h :: t => h + t.length 
  case _ => 0
} 

lst match { 
  case ::(h, t) => println(h); println(t)
  case _ => 0
}  

// :: 将元素添加到List最前面

// 从前往后匹配
List(1, 7, 2, 9) match { 
  case first :: second :: rest => println(first); println(second); println(rest)
  case _ => 0
}

List(1, 7, 2, 9) match { 
  case ::(first, ::(second, rest)) => println(first); println(second); println(rest)
  case _ => 0
}

List(List(1, 7), List(2, 9)) match { 
  case (first :: second) :: rest1 => println(first); println(second); println(rest1)
  case _ => 0
}

// Infix notation works with any binary unapply--doesn't have to 
// come from case class

case object +: {
  def unapply[T](input: List[T]) = 
    if (input.isEmpty) None else Some((input.head, input.tail))
}

1 +: 7 +: 2 +: 9 +: Nil match { 
  case first +: second +: rest => println(first); println(second); println(rest)
}
