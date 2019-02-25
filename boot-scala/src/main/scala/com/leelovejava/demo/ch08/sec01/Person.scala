package com.sec01.impatient {
  package people {
    class Person(val name: String) {
      val friends = new collection.mutable.ArrayBuffer[Person]
      // Doesn't pick up collection from com.horstmann.collection
      def description = name + " with friends " + 
        friends.map(_.name).mkString(", ")
    }
  }
}


// Run as scala com.horstmann.collection.Main

package com.sec01.collection {
  object Sec01Main extends App {
    val fred = new com.sec01.impatient.people.Person("Fred")
    val wilma = new com.sec01.impatient.people.Person("Wilma")
    val barney = new com.sec01.impatient.people.Person("Barney")
    fred.friends += wilma
    fred.friends += barney
    println(fred.description)
  }  
}
