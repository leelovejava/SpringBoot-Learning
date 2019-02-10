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

    //unionTest

    //partitionByTest

    //orderTest

    //groupBy

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
  def sampleTest: Unit = {
    val rdd = sc.parallelize(1 to 10)
    // res4: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // 有放回抽样
    println(rdd.sample(true, 0.4, 2).collect())
    // res0: Array[Int] = Array(1, 2, 2)

    println(rdd.sample(false, 0.4, 2).collect())
    // res1: Array[Int] = Array(1, 3)

    /**
      * takeSample(withReplacement,num,seed)
      * 和Sample的区别是：takeSample返回的是最终的结果集合。
      */
    println(rdd.takeSample(false, 1, 2))
    // res10: Array[Int] = Array(4)
  }


  def unionTest: Unit = {
    val rdd1 = sc.parallelize(1 to 5)

    val rdd2 = sc.parallelize(2 to 6)

    /**
      * union(otherDataset)	对源RDD和参数RDD求并集后返回一个新的RDD
      */
    println(rdd1.union(rdd2).collect().mkString(","))
    // res11: Array[Int] = Array(1, 2, 3, 4, 5, 2, 3, 4, 5, 6)

    /**
      * intersection(otherDataset)	对源RDD和参数RDD求交集后返回一个新的RDD
      */
    println(rdd1.intersection(rdd2).collect().mkString(","))
    // res12: Array[Int] = Array(4, 2, 3, 5)

    /**
      * cartesian(otherDataset)	笛卡尔积
      */
    println(rdd1.cartesian(rdd2).collect().mkString(","))
    // res22: Array[(Int, Int)] = Array((1,2), (1,3), (2,2), (2,3), (1,4), (1,5), (1,6), (2,4), (2,5), (2,6), (3,2), (3,3), (4,2), (4,3), (5,2), (5,3), (3,4), (3,5), (3,6), (4,4), (4,5), (4,6), (5,4), (5,5), (5,6))

    /**
      * distinct([numTasks])) 去重
      * 对源RDD进行去重后返回一个新的RDD. 默认情况下，只有8个并行任务来操作，但是可以传入一个可选的numTasks参数改变它。
      */
    val rdd3 = sc.parallelize(List(1, 2, 3, 4, 5, 2, 3, 4, 5, 6))
    println(rdd3.distinct(2).collect().mkString(","))
    println(rdd3.distinct().collect().mkString(","))
  }

  /**
    * partitionBy
    * 对RDD进行分区操作，如果原有的partionRDD和现有的partionRDD是一致的话就不进行分区， 
    * 否则会生成ShuffleRDD. 
    */
  def partitionByTest: Unit = {
    var rdd = sc.parallelize(Array((1, "aaa"), (2, "bbb"), (3, "ccc")))
    println(rdd.partitions.size)
    var rdd2 = rdd.partitionBy(new org.apache.spark.HashPartitioner(3))
    println(rdd2.partitions.size)
  }

  /**
    * groupByKey
    * 对相同键的值进行分组
    * 对每个key进行操作，只生成一个sequence
    */
  def groupByKey: Unit = {
    var words = Array("aaa", "bbb", "ccc")
    val wordPairsRDD = sc.parallelize(words).map(word => (word, 1))
    // res14: Array[(String, Int)] = Array((aaa,1), (bbb,1), (ccc,1))
    println(wordPairsRDD.groupByKey().collect())
    // res13: Array[(String, Iterable[Int])] = Array((bbb,CompactBuffer(1)), (ccc,CompactBuffer(1)), (aaa,CompactBuffer(1)))
  }

  /**
    * 分组
    * def groupBy[K: ClassTag](f: T => K): RDD[(K, Iterable[T])]
    * def groupBy[K: ClassTag](f: T => K, numPartitions: Int): RDD[(K, Iterable[T])]
    * def groupBy[K: ClassTag](f: T => K, p: Partitioner): RDD[(K, Iterable[T])]
    */
  def groupBy:Unit={
    val a = sc.parallelize(1 to 9, 3)
    // 接收一个函数，将函数返回的值作为key，根据key进行分组
    print(a.groupBy(x => { if (x % 2 == 0) "even" else "odd" }).collect)
    // res0: Array[(String, Iterable[Int])] = Array((even,CompactBuffer(2, 4, 6, 8)), (odd,CompactBuffer(1, 3, 5, 7, 9)))
  }

  /**
    * 排序
    */
  def orderTest: Unit = {
    val rdd = sc.parallelize(Array((3, "aa"), (6, "cc"), (2, "bb"), (1, "dd")))

    /**
      * keys() 返回一个仅包含key的RDD
      */
    println(rdd.keys)

    /**
      * values() 返回一个仅包含value的RDD
      */
    println(rdd.values)

    /**
      * sortByKey([ascending], [numTasks])
      * 根据键进行排序
      * 在一个(K,V)的RDD上调用，K必须实现Ordered接口，返回一个按照key进行排序的(K,V)的RDD
      */
    rdd.sortByKey(true).collect()
    // res16: Array[(Int, String)] = Array((1,dd), (2,bb), (3,aa), (6,cc))

    rdd.sortByKey(false).collect()
    // res17: Array[(Int, String)] = Array((6,cc), (3,aa), (2,bb), (1,dd))

    /**
      * sortBy(func,[ascending], [numTasks])
      * 与sortByKey类似，但是更灵活,可以用func先对数据进行处理，按照处理后的数据比较结果排序
      */
    // 第一个元素+1,再进行排序
    rdd.sortBy(x => x._1 + 1).collect()
    // res19: Array[(Int, String)] = Array((1,dd), (2,bb), (3,aa), (6,cc))
  }

  /**
    * 连接
    */
  def join: Unit = {
    val rdd = sc.parallelize(Array((3, "aa"), (6, "cc"), (2, "bb"), (1, "dd")))
    val rdd1 = sc.parallelize(Array((1, 4), (2, 5), (3, 6)))

    /**
      * join(otherDataset, [numTasks])
      * 内连接
      * 在类型为(K,V)和(K,W)的RDD上调用，返回一个相同key对应的所有元素对在一起的(K,(V,W))的RDD
      */
    rdd.join(rdd1).collect()
    // res20: Array[(Int, (String, Int))] = Array((2,(bb,5)), (1,(dd,4)), (3,(aa,6)))

    /**
      * 右外连接
      */
    rdd.rightOuterJoin(rdd1).collect()

    /**
      * 左外连接
      */
    rdd.leftOuterJoin(rdd1).collect()
  }

  /**
    * subtract(otherDataset)
    * 计算差的一种函数去除两个RDD中相同的元素，不同的RDD将保留下来 
    */
  def subtractTest: Unit = {
    val rdd = sc.parallelize(1 to 5)
    val rdd1 = sc.parallelize(2 to 6)
    rdd.subtract(rdd1).collect()
    // res25: Array[Int] = Array(1)
  }

  /**
    * 数值RDD的统计操作
    */
  def numTest: Unit = {
    val rdd = sc.parallelize(1 to 10)

    /**
      * rdd中的元素个数
      */
    rdd.count()
    // res6: Long = 10

    /**
      * 元素的平均值
      */
    rdd.mean()
    // res7: Double = 5.5

    /**
      * 总和
      */
    rdd.sum()
    // res8: Double = 55.0

    /**
      * 最大值
      */
    rdd.max()
    // res9: Int = 10

    /**
      * 最小值
      */
    rdd.min()
    // res10: Int = 1

    /**
      * 元素的方差
      */
    rdd.variance()
    // res11: Double = 8.25

    /**
      * 从采样中计算出方差
      */
    rdd.sampleVariance()
    // res12: Double = 9.166666666666666

    /**
      * 标准差
      */
    rdd.stdev()
    // res13: Double = 2.8722813232690143

    /**
      * 采样的标准差
      */
    rdd.sampleStdev()
    // res14: Double = 3.0276503540974917
  }
}