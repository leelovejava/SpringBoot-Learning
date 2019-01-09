package com.atguigu.sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

/**
  * Spark core wordCount
  * Created by wuyufei on 31/07/2017.
  */
object WordCount {

  val logger = LoggerFactory.getLogger(WordCount.getClass)

  def main(args: Array[String]) {
    //创建SparkConf()并设置App名称
    val conf = new SparkConf().setMaster("local[3]").setAppName("WC")
    //创建SparkContext，该对象是提交spark App的入口
    val sc = new SparkContext(conf)

    //使用sc创建RDD并执行相应的transformation和action
    sc.textFile(args(0)).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_, 1).sortBy(_._2, false).saveAsTextFile(args(1))
    //停止sc，结束该任务

    

    logger.info("complete!")

    sc.stop()

  }

}








