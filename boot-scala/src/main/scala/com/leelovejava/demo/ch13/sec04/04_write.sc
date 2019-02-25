// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 04_write.sc to run them all at once.

import java.io._

val out = new PrintWriter("numbers.txt")
for (i <- 1 to 10) out.print("%6d %10.6f\n".format(i, 1.0 / i))
out.close()
