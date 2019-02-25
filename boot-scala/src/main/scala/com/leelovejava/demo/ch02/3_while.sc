// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

var r = 1
var n = 10
// while 循环
while (n > 0) {
  r = r * n
  n -= 1
  println(r)
}

r = 1
n = 10

// do while 循环
do{
  r = r * n
  n -= 1
  println(r)
}while(n > 0)


import scala.util.control.Breaks._

r = 1
n = 10
// break
breakable {
  while (n > 0) {
    r = r * n
    n -= 1
    println(r)
    if(r == 720) break;
  }
}
