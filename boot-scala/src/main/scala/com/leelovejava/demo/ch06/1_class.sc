// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

// class定义类，和java类似，不用声明public
class Counter {
  private var value = 0 // You must initialize the field
  def increment() { value += 1 } // Methods are public by default
  def current() = value
}
// new Counter() 也可以
val myCounter = new Counter

myCounter.increment()
println(myCounter.current)

val myCounter2 = new Counter() // () ok
myCounter2.current() // () ok

class Counter1 {
  private var value = 0 
  def increment() { value += 1 } 
  def current = value // No () in definition
}

val myCounter1 = new Counter
myCounter1.current() // () not ok
