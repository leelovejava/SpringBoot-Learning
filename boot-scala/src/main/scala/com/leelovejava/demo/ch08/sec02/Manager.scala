package com.leelovejava.demo.ch08.sec02

class Manager {
        // The following doesn't work (look into Other.scala)
        // val subordinates = new collection.mutable.ArrayBuffer[Employee]
        val subordinates = new _root_.scala.collection.mutable.ArrayBuffer[Employee]
        def description = "A manager with " + subordinates.length + " subordinates"
      }
