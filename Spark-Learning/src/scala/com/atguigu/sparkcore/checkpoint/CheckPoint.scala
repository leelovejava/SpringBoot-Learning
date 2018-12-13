package com.atguigu.sparkcore.checkpoint

import org.apache.spark.{SparkConf, SparkContext}

/**
  * rdd的检查点机制
  */
class CheckPoint {

  /**
    * cache 和 checkpoint的区别
    *
    * 缓存把 RDD 计算出来然后放在内存中，但是RDD 的依赖链（相当于数据库中的redo 日志）， 也不能丢掉， 当某个点某个 executor 宕了，上面cache 的RDD就会丢掉， 需要通过 依赖链重放计算出来
    * (依赖链不可丢)
    *
    * checkpoint 是把 RDD 保存在 HDFS中， 是多副本可靠存储，所以依赖链就可以丢掉了，就斩断了依赖链， 是通过复制实现的高容错
    * (依赖链可丢,多副本存储)
    *
    * 检查点机制使用场景:
    * 1) DAG中的Lineage过长，如果重算，则开销太大（如在PageRank中）。
    *
    * 2) 在宽依赖上做Checkpoint获得的收益更大
    */

  //SparkContext.setCheckpointDir()

  def main(args: Array[String]): Unit = {
    // 创建SparkConf()并设置App名称
    val conf = new SparkConf().setMaster("local[2]").setAppName("Transformation")
    // 创建SparkContext，该对象是提交spark App的入口
    val sc = new SparkContext(conf)
    val data = sc.parallelize(1 to 10)
    sc.setCheckpointDir("hdfs://hadoop000:9000/checkpoint")
    data.checkpoint
    data.count

  }

}
