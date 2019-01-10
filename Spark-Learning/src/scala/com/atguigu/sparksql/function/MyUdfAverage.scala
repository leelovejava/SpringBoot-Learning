package com.atguigu.sparksql.function

import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}

/**
  * Spark SQL自定义udf函数
  * @param name
  * @param salary
  */
// 既然是强类型，可能有case类
case class Employee(name: String, salary: Long)
case class Average(var sum: Long, var count: Long)

object MyAverage extends Aggregator[Employee, Average, Double] {
  // 定义一个数据结构，保存工资总数和工资总个数，初始都为0
  def zero: Average = Average(0L, 0L)
  // Combine two values to produce a new value. For performance, the function may modify `buffer`
  // and return it instead of constructing a new object
  def reduce(buffer: Average, employee: Employee): Average = {
    buffer.sum += employee.salary
    buffer.count += 1
    buffer
  }
  // 聚合不同execute的结果
  def merge(b1: Average, b2: Average): Average = {
    b1.sum += b2.sum
    b1.count += b2.count
    b1
  }
  // 计算输出
  def finish(reduction: Average): Double = reduction.sum.toDouble / reduction.count
  // 设定之间值类型的编码器，要转换成case类
  // Encoders.product是进行scala元组和case类转换的编码器
  def bufferEncoder: Encoder[Average] = Encoders.product
  // 设定最终输出值的编码器
  def outputEncoder: Encoder[Double] = Encoders.scalaDouble

  def main(args: Array[String]) {
    //创建SparkConf()并设置App名称
    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    //val ds = spark.read.json("examples/src/main/resources/employees.json").as[Employee]
    //ds.show()

    //val averageSalary = MyAverage.toColumn.name("average_salary")
    //val result = ds.select(averageSalary)
    //result.show()

    spark.stop()
  }
}
