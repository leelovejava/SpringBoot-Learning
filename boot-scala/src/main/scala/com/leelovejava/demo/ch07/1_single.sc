// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

object Accounts {
  println("init")
  private var lastNumber = 0
  val abc = "wu"
  def newUniqueNumber() = { lastNumber += 1; lastNumber }
}

Accounts.newUniqueNumber()
Accounts.newUniqueNumber()
Accounts.abc