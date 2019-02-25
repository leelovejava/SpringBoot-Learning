import scala.collection.mutable.ArrayBuffer

class Network(val name: String) { outer =>  
  class Member(val name: String) {
    val contacts = new ArrayBuffer[Member]
    def description = name + " inside " + outer.name
  }

  private val members = new ArrayBuffer[Member]

  def join(name: String) = {
    val m = new Member(name)
    members += m
    m
  }
}

val chatter = new Network("Chatter")
val myFace = new Network("MyFace")

val fred = chatter.join("Fred")
println(fred.description);
val barney = myFace.join("Barney")
println(barney.description);
