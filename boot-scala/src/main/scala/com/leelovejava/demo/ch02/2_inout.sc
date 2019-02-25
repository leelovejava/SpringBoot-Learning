// These are meant to be typed into the REPL. You can also run
// scala -Xnojline -Xnojline < repl-session.scala to run them all at once.

print("Answer: ")
println(42)

println("Answer: " + 42)

printf("Hello, %s! You are %d years old.%n", "Fred", 42)

val name = "Fred"
val age = 42.333333

print(f"Hello, $name! You are $age%7.2f years old%n")

import scala.io._
val name2 = StdIn.readLine("Your name: ")

print("Your age: ")
val age2 = StdIn.readInt()

println(f"Hello, $name2! Next year, you will be ${age2 + 1}.", name, age + 1)

