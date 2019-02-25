// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

import java.awt.{Color, Font}

Color.RED
Font.BOLD

import java.util.{HashMap => JavaHashMap}
import scala.collection.mutable._

new JavaHashMap[String, Int]
new HashMap[String, Int]

import java.util.{HashSet => _, _}
import scala.collection.mutable._

new HashSet[String]
