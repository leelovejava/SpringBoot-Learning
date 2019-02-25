// These are meant to be typed into the REPL. You can also run
// scala -Xnojline < 06_process.sc to run them all at once.

import scala.sys.process._

"ls -al ..".!

val result = "ls -al ..".!!

val outcome = ("ls -al .." #| "grep sec0").!

import java.io.File

"ls -al .." #> new File("filelist.txt") !
"ls -al .." #>> new File("filelist.txt") !
"grep sec" #< new File("filelist.txt") !

import java.net.URL

"grep Scala" #< new URL("http://horstmann.com/index.html") !

val cmd = "scala script.scala"
val dirName = "../sec03"
val p = Process(cmd, new File(dirName), ("LANG", "en_US"))
"echo 42" #| p !
