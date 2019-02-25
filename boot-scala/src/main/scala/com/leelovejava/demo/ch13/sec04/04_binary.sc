// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 04_binary.sc to run them all at once.

import java.io._

val file = new File("repl-session.zip")
val in = new FileInputStream(file)
val bytes = new Array[Byte](file.length.toInt)
in.read(bytes)
in.close()

print(f"Zip files starts with ${bytes(0)}%c${bytes(1)}%c, the initials of their inventor.%n")
