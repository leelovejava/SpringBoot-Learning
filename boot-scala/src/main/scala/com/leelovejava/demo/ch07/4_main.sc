object Hello {
  def main(args: Array[String]) {
    println("Hello, World!")
  }
}

object Hello1 extends App {
  if (args.length > 0)
    println("Hello, " + args(0))
  else
    println("Hello, World!")
}