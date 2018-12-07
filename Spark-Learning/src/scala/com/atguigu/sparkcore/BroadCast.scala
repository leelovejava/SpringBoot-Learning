package com.atguigu.sparkcore

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 广播变量
  * 高效分发较大的对象
  */
object BroadCast {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("BroadCast")
    val sc = new SparkContext(conf)
    val broadcastVar = sc.broadcast(Array(1, 2, 3))
    broadcastVar.value
  }
}
