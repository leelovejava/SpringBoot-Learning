// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

class Counter {
  private var value = 0
  def increment() { value += 1 }

  def isLess(other : Counter) = value < other.value 
    // Can access private field of other object
}

class Counter2 {
  private[this] var value = 0
  def increment() { value += 1 }

  def isLess(other : Counter) = value < other.value 
    // Can't access private[this] field of other object
}



