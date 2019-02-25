// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

import scala.collection.mutable.ArrayBuffer

class Network {
  class Member(val name: String) {
    val contacts = new ArrayBuffer[Member]
  }

  private val members = new ArrayBuffer[Member]

  def join(name: String) = {
    val m = new Member(name)
    members += m
    m
  }
}

val chatter = new Network
val myFace = new Network

val fred = chatter.join("Fred")
val wilma = chatter.join("Wilma")
fred.contacts += wilma // OK
val barney = myFace.join("Barney") // Has type myFace.Member
fred.contacts += barney // No
  // Can’t add a myFace.Member to a buffer of chatter.Member elements

class Network1 {
  class Member(val name: String) {
    val contacts = new ArrayBuffer[Network1#Member]
  }

  private val members = new ArrayBuffer[Member]

  def join(name: String) = {
    val m = new Member(name)
    members += m
    m
  }
}

val chatter1 = new Network1
val myFace1 = new Network1

val fred1 = chatter1.join("Fred")
val wilma1 = chatter1.join("Wilma")
fred1.contacts += wilma1 // OK
val barney1 = myFace1.join("Barney")
fred1.contacts += barney1 // Also OK
