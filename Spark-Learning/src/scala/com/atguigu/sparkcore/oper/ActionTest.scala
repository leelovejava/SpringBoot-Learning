package com.atguigu.sparkcore.oper

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark操作编程之Action 行动操作
  */
object ActionTest {
  // 创建SparkConf()并设置App名称
  private val conf = new SparkConf().setMaster("local[2]").setAppName("Transformation")
  // 创建SparkContext，该对象是提交spark App的入口
  private val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

  }

  /**
    * reduce(func)
    * 通过func函数聚集RDD中的所有元素，这个功能必须是可交换且可并联的
    */
  def reduce: Unit = {
    val rdd1 = sc.makeRDD(1 to 10, 2)
    rdd1.reduce(_ + _)
    // res26: Int = 55

    val rdd2 = sc.makeRDD(Array(("a", 1), ("a", 3), ("c", 3), ("d", 5)))
    // res28: Array[(String, Int)] = Array((a,1), (a,3), (c,3), (d,5))
    rdd2.reduce((x, y) => (x._1 + y._1, x._2 + y._2))
    // res27: (String, Int) = (aacd,12)
  }

  /**
    * collect()
    *
    * 在驱动程序中，以数组的形式返回数据集的所有元素
    */
  def collect: Unit = {
    val rdd = sc.parallelize(1 to 5)
    rdd.collect()
  }

  /**
    * 统计
    */
  def count: Unit = {
    /**
      * count()	返回RDD的元素个数
      */
    var rdd = sc.parallelize(1 to 10)
    println(rdd.count())

    /**
      * countByKey()	针对(K,V)类型的RDD，返回一个(K,Int)的map，表示每一个key对应的元素个数。
      */
    val rdd2 = sc.parallelize(List((1, 3), (1, 2), (1, 4), (2, 3), (3, 6), (3, 8)), 3)
    rdd2.countByKey()
    // res63: scala.collection.Map[Int,Long] = Map(3 -> 2, 1 -> 3, 2 -> 1)
  }

  /**
    * first()	返回RDD的第一个元素（类似于take(1)）
    */
  def first: Unit = {
    val rdd = sc.parallelize(1 to 5)
    println(rdd.first())
  }


  def take: Unit = {

    val rdd = sc.makeRDD(1 to 5)

    /**
      * take(n)	返回一个由数据集的前n个元素组成的数组
      */
    rdd.take(1)
    // res29: Array[Int] = Array(1)

    /**
      * takeSample(withReplacement,num, [seed])
      * 返回一个数组，该数组由从数据集中随机采样的num个元素组成，可以选择是否用随机数替换不足的部分，seed用于指定随机数生成器种子
      */
    rdd.takeSample(true, 5, 3)
    // res30: Array[Int] = Array(4, 3, 3, 3, 5)

    /**
      * takeOrdered(n)	返回前几个的排序
      */
    rdd.takeOrdered(4)
    // res32: Array[Int] = Array(1, 2, 3, 4)
  }


  def save: Unit = {
    val rdd = sc.parallelize(1 to 100)

    /**
      * saveAsTextFile(path)
      * 将数据集的元素以textfile的形式保存到HDFS文件系统或者其他支持的文件系统，对于每个元素，Spark将会调用toString方法，将它装换为文件中的文本
      */
    rdd.saveAsTextFile("hdfs://hadoop000:9000/rdd")

    /**
      * saveAsSequenceFile(path) 	将数据集中的元素以Hadoop sequencefile的格式保存到指定的目录下，可以使HDFS或者其他Hadoop支持的文件系统。
      */
    val data=sc.parallelize(List((2,"aa"),(3,"bb"),(4,"cc"),(5,"dd"),(6,"ee")))
    data.saveAsSequenceFile("hdfs://hadoop000:9000/sequdata")

    /**
      * saveAsObjectFile(path) 	用于将RDD中的元素序列化成对象，存储到文件中。
      */
    rdd.saveAsObjectFile("hdfs://hadoop000:9000/object")
  }

  /**
    * foreach(func)	在数据集的每一个元素上，运行函数func进行更新。
    */
  def foreach: Unit = {
    val rdd = sc.makeRDD(1 to 10)

    val sum = sc.longAccumulator
    // res0: org.apache.spark.util.LongAccumulator = LongAccumulator(id: 0, name: None, value: 0)
    //sc.longAccumulator("My Accumulator")

    // 2.2.0之前
    // sc.accumulable(0)
    // rdd.foreach(sum += _)

    rdd.foreach(x => sum.add(x))
    println(sum.value)
    // res5: Long = 55
  }

}
