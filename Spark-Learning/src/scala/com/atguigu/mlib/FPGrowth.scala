package com.atguigu.mlib

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.{SparkConf, SparkContext}

object FPGrowth extends App{

  //屏蔽日志
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

  //创建SparkContext
  val conf = new SparkConf().setMaster("local[4]").setAppName("FPGrowth")
  val sc = new SparkContext(conf)

  //加载数据样本
  val path = "Spark-Learning/src/resources/ml/fpgrowth/fpgrowth.txt";
  //创建交易样本
  val transactions = sc.textFile(path).map(_.split(" ")).cache()

  println(s"交易样本的数量为： ${transactions.count()}")

  //最小支持度
  val minSupport = 0.4

  //计算的并行度
  val numPartition = 2

  //训练模型
  val model = new FPGrowth()
    .setMinSupport(minSupport)
    .setNumPartitions(numPartition)
    .run(transactions)

  //打印模型结果
  println(s"经常一起购买的物品集的数量为： ${model.freqItemsets.count()}")
  model.freqItemsets.collect().foreach { itemset =>
    println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)
  }

  sc.stop()

}
