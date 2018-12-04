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

    //mapTest

    //floatMapTest

    //filterTest

    //mapPartitionsTest()

    //sampleTest

    unionTest

    // 停止sc，结束该任务
    sc.stop()
  }

  /**
    * map(func)
    * 参数是函数,函数应用于RDD每一个元素,返回值是新的RDD
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
    * flatMap()
    * 参数是函数，函数应用于RDD每一个元素，将元素数据进行拆分，变成迭代器，返回值是新的RDD
    */
  def floatMapTest: Unit = {
    println("*******flatMap**************")
    val floatRDD = sc.parallelize(Array(("A", 1), ("B", 2), ("C", 3)))
    val sourceRDD = sc.parallelize(1 to 10)
    println(floatRDD.flatMap(x => (x._1 + x._2)).collect().mkString)
    println(sourceRDD.flatMap(1 to _).collect().mkString(","))
  }

  /**
    * filter()
    * 参数是函数，函数会过滤掉不符合条件的元素，返回值是新的RDD
    */
  def filterTest: Unit = {
    println("*******filter**************")


    val sourceFilter = sc.parallelize(Array("xiaoming", "xiaojiang", "xiaohe", "dazhi"))
    val filter = sourceFilter.filter(_.contains("xiao"))
    println(sourceFilter.collect().mkString(","))
    println(filter.collect().mkString(","))
  }

  /**
    * mapPartitions(func)
    * 类似于map，但独立地在RDD的每一个分片上运行，因此在类型为T的RDD上运行时，func的函数类型必须是Iterator[T] => Iterator[U]。
    * 假设有N个元素，有M个分区，那么map的函数的将被调用N次,而mapPartitions被调用M次,一个函数一次处理所有分区
    */
  def mapPartitionsTest(): Unit = {
    println("******* mapPartitions**************")

    val rdd = sc.parallelize(List(("春菜", "春奈"), ("遥奈", "晴南"), ("新垣结衣", "あらがき ゆい"), ("堀北真希", "ほりきた まき")))
    val mapPartitionsRDD = rdd.mapPartitions(partitionsFun)
    println(mapPartitionsRDD.collect())
  }

  def partitionsFun(iter: Iterator[(String, String)]): Iterator[(String)] = {
    var woman = List[String]()
    // 迭代器
    while (iter.hasNext) {
      val next = iter.next()
      // 模式匹配
      next match {
        case (_, "新垣结衣") => woman = next._1 :: woman
        // 不符合条件
        case _ =>
      }
    }
    return woman.iterator
  }

  /**
    * sample(withReplacement, fraction, seed)
    * 以指定的随机种子随机抽样出数量为fraction的数据，withReplacement表示是抽出的数据是否放回，true为有放回的抽样，false为无放回的抽样，seed用于指定随机数生成器种子。
    * 例子从RDD中随机且有放回的抽出50%的数据，随机种子值为3（即可能以1 2 3的其中一个起始值）
    */
  def sampleTest: Unit ={
    val rdd = sc.parallelize(1 to 10)
    // res4: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // 有放回抽样
    println(rdd.sample(true,0.4,2).collect())
    // res0: Array[Int] = Array(1, 2, 2)

    println(rdd.sample(false,0.4,2).collect())
    // res1: Array[Int] = Array(1, 3)

    /**
      * takeSample(withReplacement,num,seed)
      * 和Sample的区别是：takeSample返回的是最终的结果集合。
      */
    println(rdd.takeSample(false,1,2))
    // res10: Array[Int] = Array(4)
  }

  /**
    * union(otherDataset)	对源RDD和参数RDD求并集后返回一个新的RDD
    */
  def unionTest: Unit ={
    val rdd1=sc.parallelize(1 to 5)

    val rdd2=sc.parallelize(2 to 6)

    println(rdd1.union(rdd2).collect().mkString(","))
    // res11: Array[Int] = Array(1, 2, 3, 4, 5, 2, 3, 4, 5, 6)

    /**
      * intersection(otherDataset)	对源RDD和参数RDD求交集后返回一个新的RDD
      */
    println(rdd1.intersection(rdd2).collect().mkString(","))
    // res12: Array[Int] = Array(4, 2, 3, 5)

    /**
      * distinct([numTasks]))
      * 对源RDD进行去重后返回一个新的RDD. 默认情况下，只有8个并行任务来操作，但是可以传入一个可选的numTasks参数改变它。
      */
    val rdd3=sc.parallelize(List(1, 2, 3, 4, 5, 2, 3, 4, 5, 6))
    println(rdd3.distinct().collect().mkString(","))
  }
}