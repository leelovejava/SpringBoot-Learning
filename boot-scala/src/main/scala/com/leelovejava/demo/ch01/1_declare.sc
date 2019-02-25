// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.sc to run them all at once.

// val不能改变
val answer = 8 * 5 + 2

0.5 * answer

val greeting: String = null
//answer = 0 // This will give an error


// var 能够改变
var counter = 0
counter = 1 // Ok, can change a var


// 多值声明
var i, j = 0
var greeting2, message: String = null

