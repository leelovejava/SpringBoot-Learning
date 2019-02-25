// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 15_partial.sc to run them all at once.

val f: PartialFunction[Char, Int] = { case '+' => 1 ; case '-' => -1 }
f('-') 
f.isDefinedAt('0')

f('0') 
