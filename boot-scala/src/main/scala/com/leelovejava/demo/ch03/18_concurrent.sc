// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < repl-session.scala to run them all at once.

(1 to 5).par.foreach{it => println(Thread.currentThread);print("^"+ it)}

(1 to 5).par.map( _ + 100).seq