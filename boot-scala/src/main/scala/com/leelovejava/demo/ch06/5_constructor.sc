// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.
import java.util.Properties
import java.io.FileReader

class Person(val name: String = "", val age: Int = 0) {
  println("Just constructed another person")
  def description = name + " is " + age + " years old"
}

val p1 = new Person
val p2 = new Person("Fred")
val p3 = new Person("Fred", 42)
p1.description
p2.description
p3.description



class MyProg {
  private val props = new Properties
  props.load(new FileReader("myprog.properties"))
  // The statement above is a part of the primary constructor
}

class Person2(val name: String, private var age: Int) {
  def description = name + " is " + age + " years old"
  def birthday() { age += 1 }
}

val p = new Person2("Fred", 42)
p.name
//p.age // Error--it's private
p.birthday()
p.description




class Person3 {
  private var name = ""
  private var age = 0

  def this(name: String) { // An auxiliary constructor
    this() // Calls primary constructor
    this.name = name
  }
  def this(name: String, age: Int) { // Another auxiliary constructor
    this(name) // Calls previous auxiliary constructor
    this.age = age
  }

  def description = name + " is " + age + " years old"
}

val p11 = new Person3 // Primary constructor
val p21 = new Person3("Fred") // First auxiliary constructor
val p31= new Person3("Fred", 42) // Second auxiliary constructor

p11.description
p21.description
p31.description














