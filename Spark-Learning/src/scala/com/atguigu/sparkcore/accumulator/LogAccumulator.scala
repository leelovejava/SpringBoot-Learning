package com.atguigu.sparkcore.accumulator

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 自定义广播变量
  */
class LogAccumulator extends org.apache.spark.util.AccumulatorV2[String, java.util.Set[String]] {
  private val _logArray: java.util.Set[String] = new java.util.HashSet[String]()

  /**
    * 判断是否为初始值
    *
    * @return
    */
  override def isZero: Boolean = {
    _logArray.isEmpty
  }

  /**
    * 重置累加器中的值
    */
  override def reset(): Unit = {
    _logArray.clear()
  }

  /**
    * 累加
    * @param v
    */
  override def add(v: String): Unit = {
    _logArray.add(v)
  }

  /**
    * 对各个task的累加器进行合并
    *
    * @param other
    */
  override def merge(other: org.apache.spark.util.AccumulatorV2[String, java.util.Set[String]]): Unit = {
    other match {
      case o: LogAccumulator => _logArray.addAll(o.value)
    }

  }

  /**
    * 获取累加器中的值
    *
    * @return
    */
  override def value: java.util.Set[String] = {
    java.util.Collections.unmodifiableSet(_logArray)
  }

  /**
    * 拷贝累加器
    *
    * @return
    */
  override def copy(): org.apache.spark.util.AccumulatorV2[String, java.util.Set[String]] = {
    val newAcc = new LogAccumulator()
    _logArray.synchronized {
      newAcc._logArray.addAll(_logArray)
    }
    newAcc
  }
}

/**
  * 过滤掉带字母的
  */
object LogAccumulator {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("LogAccumulator")
    val sc = new SparkContext(conf)

    val accum = new LogAccumulator
    sc.register(accum, "logAccum")
    val sum = sc.parallelize(Array("1", "2a", "3", "4b", "5", "6", "7cd", "8", "9"), 2).filter(line => {
      val pattern = """^-?(\d+)"""
      val flag = line.matches(pattern)
      if (!flag) {
        accum.add(line)
      }
      flag
    }).map(_.toInt).reduce(_ + _)

    println("sum: " + sum)
    //for (v <- accum.value) print(v + "")
    println()
    sc.stop()
  }
}
