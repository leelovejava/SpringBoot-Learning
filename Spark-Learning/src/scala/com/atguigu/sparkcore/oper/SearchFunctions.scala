package com.atguigu.sparkcore.oper

/**
  * rdd编程
  * 向RDD操作传递函数
  *
  * 传递的函数及其引用的数据需要是可序列化的(实现了 Java 的 Serializable 接口)
  *
  * 传递一个对象的方法或者字段时，会包含对整个对象的引用
  * @param query
  */
class SearchFunctions(val query: String) extends java.io.Serializable {
  def isMatch(s: String): Boolean = {
    s.contains(query)
  }

  def getMatchesFunctionReference(rdd: org.apache.spark.rdd.RDD[String]): org.apache.spark.rdd.RDD[String] = {
    // 问题:"isMatch"表示"this.isMatch"，因此我们要传递整个"this" 
    rdd.filter(isMatch)
  }

  def getMatchesFieldReference(rdd: org.apache.spark.rdd.RDD[String]): org.apache.spark.rdd.RDD[String] = {
    // 问题:"query"表示"this.query"，因此我们要传递整个"this"
    rdd.filter(x => x.contains(query))
  }

  def getMatchesNoReference(rdd: org.apache.spark.rdd.RDD[String]): org.apache.spark.rdd.RDD[String] = {
    // 安全:只把我们需要的字段拿出来放入局部变量中 
    val query_ = this.query
    rdd.filter(x => x.contains(query_))
  }
} 