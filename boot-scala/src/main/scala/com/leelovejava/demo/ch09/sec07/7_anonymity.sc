// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

class Person(val name: String) {
  override def toString = getClass.getName + "[name=" + name + "]"
}

val alien = new Person("Fred") {
  def greeting = "Greetings, Earthling! My name is Fred."
}

def meet(p: Person{def greeting: String}) {
  println(p.name + " says: " + p.greeting)
}

meet(alien)
