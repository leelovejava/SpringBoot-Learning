package com.atguigu.sparkcore.oper

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark编程练习
  */
object PracticeTest {
  // 创建SparkConf()并设置App名称
  private val conf = new SparkConf().setMaster("local[2]").setAppName("PracticeTest")
  // 创建SparkContext，该对象是提交spark App的入口
  private val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {
    reduceTest
  }

  def reduceTest: Unit = {
    val rdd1 = sc.parallelize(List(("tom", 1), ("jerry", 3), ("kitty", 2), ("shuke", 1)))
    val rdd2 = sc.parallelize(List(("jerry", 2), ("tom", 3), ("shuke", 2), ("kitty", 5)))
    val rdd3 = rdd1.union(rdd2)
    // 按key进行聚合
    val rdd4 = rdd3.reduceByKey(_ + _)
    rdd4.collect
    // res14: Array[(String, Int)] = Array((tom,4), (jerry,5), (shuke,3), (kitty,7))

    //按value的降序排序
    val rdd5 = rdd4.map(t => (t._2, t._1)).sortByKey(false).map(t => (t._2, t._1))
    rdd5.collect
    //res15: Array[(String, Int)] = Array((kitty,7), (jerry,5), (tom,4), (shuke,3))
  }
}
