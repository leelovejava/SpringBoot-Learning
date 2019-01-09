package com.atguigu.sparksql.function

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
  * Spark自定义聚合函数
  */
object MyAggrAverage extends UserDefinedAggregateFunction {
  // 聚合函数输入参数的数据类型
  def inputSchema: StructType = StructType(StructField("inputColumn", LongType) :: Nil)
  // 聚合缓冲区中值得数据类型
  def bufferSchema: StructType = {
    StructType(StructField("sum", LongType) :: StructField("count", LongType) :: Nil)
  }
  // 返回值的数据类型
  def dataType: DataType = DoubleType
  // 对于相同的输入是否一直返回相同的输出。
  def deterministic: Boolean = true
  // 初始化
  def initialize(buffer: MutableAggregationBuffer): Unit = {
    // 存工资的总额
    buffer(0) = 0L
    // 存工资的个数
    buffer(1) = 0L
  }
  // 相同Execute间的数据合并。
  def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (!input.isNullAt(0)) {
      buffer(0) = buffer.getLong(0) + input.getLong(0)
      buffer(1) = buffer.getLong(1) + 1
    }
  }
  // 不同Execute间的数据合并
  def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }
  // 计算最终结果
  def evaluate(buffer: Row): Double = buffer.getLong(0).toDouble / buffer.getLong(1)

  def main(args: Array[String]) {
    //创建SparkConf()并设置App名称
    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames

    spark.udf.register("myAverage", MyAggrAverage)

    val df = spark.read.json("examples/src/main/resources/employees.json")
    df.createOrReplaceTempView("employees")
    df.show()


    val result = spark.sql("SELECT myAverage(salary) as average_salary FROM employees")
    result.show()

    spark.stop()
  }
}
