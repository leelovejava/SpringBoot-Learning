// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 9_transfer.sc to run them all at once.

// This invariant Pair class doesn't allow conversion of a 
// Pair[Student] to a Pair[Person]

class Person(name: String) {
  override def toString = getClass.getName + " " + name
}

class Student(name: String) extends Person(name)

class Pair[T](val first: T, val second: T) {
  override def toString = "(" + first + "," + second + ")"
}

def makeFriends(p: Pair[Person]) =
  p.first + " and " + p.second + " are now friends."

val fred = new Student("Fred")
val wilma = new Student("Wilma")

val studentPair = new Pair(fred, wilma)

makeFriends(studentPair) // Error

// 需要定义正向形变
// Making Pair covariant allows conversion of a 
// Pair[Student] to a Pair[Person]

class Pair[+T](val first: T, val second: T) {
  override def toString = "(" + first + "," + second + ")"
}

def makeFriends(p: Pair[Person]) =
  p.first + " and " + p.second + " are now friends."

val studentPair = new Pair(fred, wilma)

makeFriends(studentPair) // OK

// This contravariant Friend trait allows conversion of a 
// Friend[Person] to a Friend[Student]

trait Friend[-T] {
  def befriend(someone: T)
}

class Person(name: String) extends Friend[Person] {
  override def toString = getClass.getName + " " + name
  def befriend(someone: Person) {
      this + " and " + someone + " are now friends."
  }
}

class Student(name: String) extends Person(name)


def makeFriendWith(s: Student, f: Friend[Student]) { f.befriend(s) }

val susan = new Student("Susan")
val fred = new Person("Fred")

makeFriendWith(susan, fred) // Ok, Fred is a Friend of any person

// A unary function has variance Function1[-A, +R]

def friends(students: Array[Student], find: Function1[Student, Person]) =
  for (s <- students) yield find(s)

def friends(students: Array[Student], find: Student => Person]) =
  for (s <- students) yield find(s)

def findFred(s: Student) = new Person("Fred")

friends(Array(susan, new Student("Barney")), findFred)


