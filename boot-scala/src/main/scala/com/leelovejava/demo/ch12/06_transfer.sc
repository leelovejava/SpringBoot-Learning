// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 06_transfer.sc to run them all at once.

def smaller[T](a: T, b: T)(implicit order: T => Ordered[T])
  = if (order(a) < b) a else b

smaller(40, 2)

smaller("Hello", "World")

def smaller1[T](a: T, b: T)(implicit order: T => Ordered[T])
  = if (a < b) a else b // Can omit call to order

smaller1(40, 2)

smaller1("Hello", "World")

