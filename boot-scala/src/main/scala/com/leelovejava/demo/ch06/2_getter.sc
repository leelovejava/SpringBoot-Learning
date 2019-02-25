// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

class Person {
  var age = 0
}

val fred = new Person
// setter方法
fred.age = 21
println(fred.age)

fred.age_=(25)
// getter方法
println(fred.age) 

class Person2 {
  private var privateAge = 0 // Make private and rename
  def age = privateAge
  def age_=(newValue: Int) {
    if (newValue > privateAge) privateAge = newValue; // Can’t get younger
  }
}

val fred1 = new Person2
fred1.age = 30
fred1.age = 21
println(fred1.age)

