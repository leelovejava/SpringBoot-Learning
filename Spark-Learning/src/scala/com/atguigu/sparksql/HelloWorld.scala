package com.atguigu.sparksql

import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

/**
  * Created by wuyufei on 31/07/2017.
  * Spark SQL Hello World
  */
object HelloWorld {

  val logger = LoggerFactory.getLogger(HelloWorld.getClass)

  def main(args: Array[String]) {
    //创建SparkConf()并设置App名称
    val spark = SparkSession
      .builder()
      //.master("spark://hadoop000:7077")
      .master("local")
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._

    //var path="examples/src/main/resources/people.json"
    val path = "D:/workspace/SpringBoot-Learning/Spark-Learning/src/resources/people.json"
    val df = spark.read.json(path)

    // Displays the content of the DataFrame to stdout
    df.show()

    df.filter($"age" > 21).show()

    df.createOrReplaceTempView("persons")

    spark.sql("SELECT * FROM persons where age > 21").show()

    spark.stop()
  }

}
