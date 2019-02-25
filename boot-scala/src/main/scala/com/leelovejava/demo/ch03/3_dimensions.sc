
// 多维数组 3行4列
val matrix = Array.ofDim[Double](3, 4) // An array of arrays

val row = 0
val column = 2

matrix(0)(2) = 17.29

matrix.length 
matrix(row) // An array
matrix(row).length 

val triangle = new Array[Array[Int]](10)

for (i <- triangle.indices) 
  triangle(i) = new Array[Int](i + 1)

triangle(0)(0) = 1

for (i <- 1 until triangle.length) {
  triangle(i)(0) = 1
  triangle(i)(i) = 1
  for (j <- 1 until triangle(i).length - 1) 
    triangle(i)(j) = triangle(i - 1)(j - 1) + triangle(i - 1)(j)
}

for (row <- triangle) {
  for (elem <- row) print(elem + " ") 
  println()
}


