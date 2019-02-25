// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

Array.apply("Mary", "had", "a", "little", "lamb")
Array(Array(1, 7), Array(2, 9))
Array(100)
new Array(100)

class Account private (val id: Int, initialBalance: Double) {
  private var balance = initialBalance
  def deposit(amount: Double) { balance += amount }
  def description = "Account " + id + " with balance " + balance
}

object Account { // The companion object
  def apply(initialBalance: Double) = 
    new Account(newUniqueNumber(), initialBalance)
  private var lastNumber = 0
  private def newUniqueNumber() = { lastNumber += 1; lastNumber }
}

val acct = Account(1000.0)
val d = acct.description


