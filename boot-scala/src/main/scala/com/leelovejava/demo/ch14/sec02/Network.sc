import scala.collection.mutable. ArrayBuffer

class Network {
  class Member(val name: String) {
    //val contacts = new ArrayBuffer[Member]
    val contacts = new ArrayBuffer[Network#Member]
    override def toString = getClass.getName + "[name=" + name + 
      ",contacts=" + contacts.map(_.name).mkString("[", ",", "]") + "]"
  }

  private val members = new ArrayBuffer[Member]

  def join(name: String) = {
    val m = new Member(name)
    members += m
    m
  }

  override def toString = getClass.getName + "[members=" + members + "]"
}

val chatter = new Network
val myFace = new Network

val fred = chatter.join("Fred")
val wilma = chatter.join("Wilma")
fred.contacts += wilma // OK
val barney = myFace.join("Barney")
fred.contacts += barney // Also OK

println("chatter=" + chatter)
println("myFace=" + myFace)
