package com.atguigu.mlib

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.{SparkConf, SparkContext}

/**
  * FPGrowth 关联规则算法
  * 概念：支持度，置信度，提升度
  *
  * 1)支持度：在所有项集中出现的可能性，项集同时含有，x与y的概率。是第一道门槛，衡量量是多少，可以理解为‘出镜率’，一般会支持初始值过滤掉低的规则。
  * 尿布和啤酒的支持度为：800/10000=8%
  * 2)置信度：在X发生的条件下，Y发生的概率。这是第二道门槛，衡量的是质量，设置最小的置信度筛选可靠的规则。
  * 尿布-》啤酒的置信度为:800/1000=80%，啤酒-》尿布的置信度为：800/2000=40%
  * 3)提升度：在含有x条件下同时含有Y的可能性（x->y的置信度）比没有x这个条件下含有Y的可能性之比：confidence(尿布=> 啤酒)/概率(啤酒)) = 80%/((2000+800)/10000) 。如果提升度=1，那就是没啥关系这两个。
  * 通过支持度和置信度可以得出强关联关系，通过提升的，可判别有效的强关联关系
  *
  * 样本如下：
  * 列子：用户，时间，消费的漫画
  * u1,20160925,成都1995,seven,神兽退散。
  * u2,20160925,成都1995,seven,six。
  * u1,20160922，成都1995，恶魔日记
  *
  * 比如产生了如下规则：
  * 规则：成都1995，seven->神兽退散
  * 这条规则：
  * 成都1995，seven的支持度2/3
  * 成都1995，seven-》神兽退散，的置信度1/2
  */
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
