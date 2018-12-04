package com.atguigu.sparkcore.oper

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark操作编程之Transformation 转换操作
  */
object Transformation {
  // 创建SparkConf()并设置App名称
  private val conf = new SparkConf().setMaster("local[2]").setAppName("Transformation")
  // 创建SparkContext，该对象是提交spark App的入口
  private val sc = new SparkContext(conf)


  def main(args: Array[String]): Unit = {

    mapTest

    floatMapTest

    filterTest

    // 停止sc，结束该任务
    sc.stop()
  }

  /**
    * 1. map(func)	参数是函数,函数应用于RDD每一个元素,返回值是新的RDD
    */
  def mapTest: Unit = {
    val source = sc.parallelize(1 to 10)
    //val source = sc.makeRDD(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

    println("*******map**************")

    val sourceRDD = source.map(_ * 2)
    println(source.collect().mkString(","))
    println(sourceRDD.collect().mkString(","))
  }

  /**
    * 2.flatMap() 参数是函数，函数应用于RDD每一个元素，将元素数据进行拆分，变成迭代器，返回值是新的RDD
    */
  def floatMapTest: Unit = {
    println("*******flatMap**************")
    val floatRDD = sc.parallelize(Array(("A", 1), ("B", 2), ("C", 3)))
    val sourceRDD = sc.parallelize(1 to 10)
    println(floatRDD.flatMap(x => (x._1 + x._2)).collect().mkString)
    println(sourceRDD.flatMap(1 to _).collect().mkString(","))
  }

  /**
    * 3.filter() 参数是函数，函数会过滤掉不符合条件的元素，返回值是新的RDD
    */
  def filterTest: Unit = {
    println("*******filter**************")


    val sourceFilter = sc.parallelize(Array("xiaoming", "xiaojiang", "xiaohe", "dazhi"))
    val filter = sourceFilter.filter(_.contains("xiao"))
    println(sourceFilter.collect().mkString(","))
    println(filter.collect().mkString(","))
  }
}
