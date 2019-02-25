import scala.beans.BeanProperty
// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.


class Person {
  @BeanProperty var name : String = _
}

val fred = new Person
fred.setName("Fred")
fred.getName
