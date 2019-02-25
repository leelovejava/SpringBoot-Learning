def appendLines(target: { def append(str: String): Any }, lines: Iterable[String]) {
  for (l <- lines) { target.append(l); target.append("\n") }
}

val lines = Array("Mary", "had", "a", "little", "lamb")

val builder = new StringBuilder
appendLines(builder, lines)
println(builder)