// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 07_context.sc to run them all at once.

class Pair[T : Ordering](val first: T, val second: T) {
  def smaller(implicit ord: Ordering[T]) =
    if (ord.compare(first, second) < 0) first else second
}

new Pair(40, 2).smaller

class Pair1[T : Ordering](val first: T, val second: T) {
  def smaller =
    if (implicitly[Ordering[T]].compare(first, second) < 0) { first
    } else second
}

new Pair1(40, 2).smaller

class Pair2[T : Ordering](val first: T, val second: T) {
  def smaller = {
    import Ordered._;
    if (first < second) first else second
  }
}

new Pair2(40, 2).smaller

import java.awt.Point

// No ordering available
//new Pair2(new Point(3, 4), new Point(2, 5)).smaller

implicit object PointOrdering extends Ordering[Point] {
  def compare(a: Point, b: Point) =
    a.x * a.x + a.y * a.y - b.x * b.x + b.y * b.y
}

// Now there is an ordering available

new Pair2(new Point(3, 4), new Point(2, 5)).smaller

// Namely this one

implicitly[Ordering[Point]]

