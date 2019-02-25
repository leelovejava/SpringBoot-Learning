// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 05_seri.sc to run them all at once.

import java.io._
import scala.collection.mutable.ArrayBuffer

class Person(val name: String) extends Serializable {
  val friends = new ArrayBuffer[Person]
  // OK—ArrayBuffer is serializable
  def description = name + " with friends " +
    friends.map(_.name).mkString(", ")
}

val fred = new Person("Fred")
val wilma = new Person("Wilma")
val barney = new Person("Barney")
fred.friends += wilma
fred.friends += barney
wilma.friends += barney
barney.friends += fred

val out = new ObjectOutputStream(new FileOutputStream("test.obj"))
out.writeObject(fred)
out.close()
val in = new ObjectInputStream(new FileInputStream("test.obj"))
val savedFred = in.readObject().asInstanceOf[Person]
in.close()

savedFred.description
savedFred.friends.map(_.description)
