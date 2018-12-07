package com.atguigu.sparkcore.cache

import org.apache.spark.{SparkConf, SparkContext}

/**
  * RDD缓存
  *
  * RDD通过persist方法或cache方法可以将前面的计算结果缓存，默认情况下 persist() 会把数据以序列化的形式缓存在 JVM 的堆空间中
  */
object Cache {
  def main(args: Array[String]): Unit = {
    // 创建SparkConf()并设置App名称
    val conf = new SparkConf().setMaster("local[2]").setAppName("Transformation")
    // 创建SparkContext，该对象是提交spark App的入口
    val sc = new SparkContext(conf)

    val rdd = sc.parallelize(1 to 10)
    val nocache = rdd.map(_.toString+"["+System.currentTimeMillis+"]")
    nocache.collect()


    val cache = rdd.map(_.toString+"["+System.currentTimeMillis+"]")
    // action触发时缓存
    cache.cache()

    /**
      * 存储级别
      *
      * 在存储级别的末尾加上“_2”来把持久化数据存为两份
      * 级别                                                                使用的空间     CPU时间   是否在内存中   是否在磁盘 备注
      * val NONE : org.apache.spark.storage.StorageLevel
      * val DISK_ONLY : org.apache.spark.storage.StorageLevel                 低           高          否              是
      * val DISK_ONLY_2 : org.apache.spark.storage.StorageLevel
      * val MEMORY_ONLY : org.apache.spark.storage.StorageLevel               高           低          否              否
      * val MEMORY_ONLY_2 : org.apache.spark.storage.StorageLevel
      * val MEMORY_ONLY_SER : org.apache.spark.storage.StorageLevel           低           高           是             否
      * val MEMORY_ONLY_SER_2 : org.apache.spark.storage.StorageLevel
      * val MEMORY_AND_DISK : org.apache.spark.storage.StorageLevel           高           中等          部分          部分  如果数据在内存中放不下,则溢出到磁盘中
      * val MEMORY_AND_DISK_2 : org.apache.spark.storage.StorageLevel
      * val MEMORY_AND_DISK_SER : org.apache.spark.storage.StorageLevel       低           高             部分         部分  如果数据在内存中放不下,则溢出到磁盘上.在内存中存放系列化后的数据
      * val MEMORY_AND_DISK_SER_2 : org.apache.spark.storage.StorageLevel
      * val OFF_HEAP : org.apache.spark.storage.StorageLevel                                                                  默认,在内存有限时，可以减少频繁GC及不必要的内存消耗，提升程序性能
      */
    //cache.persist(org.apache.spark.storage.StorageLevel.MEMORY_ONLY)

    // 缓存有可能丢失，或者存储存储于内存的数据由于内存不足而被删除，RDD的缓存容错机制保证了即使缓存丢失也能保证计算的正确执行
    // 通过基于RDD的一系列转换，丢失的数据会被重算，由于RDD的各个Partition是相对独立的，因此只需要计算丢失的部分即可，并不需要重算全部Partition

    cache.collect()
    // res17: Array[String] = Array(1[1544089555831], 2[1544089555831], 3[1544089555831], 4[1544089555831], 5[1544089555831], 6[1544089555837], 7[1544089555838], 8[1544089555839], 9[1544089555841], 10[1544089555841])
  }
}
