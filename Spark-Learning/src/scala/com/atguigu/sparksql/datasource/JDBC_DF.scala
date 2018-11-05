package com.atguigu.sparksql.datasource

import java.util.Properties

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by wuyufei on 31/07/2017.
  * Spark SQL:Spark数据源之JDBC
  */
object JDBC_DF {


  /**
    * 异常:
    * Caused by: java.sql.SQLException: Incorrect string value: '\xE5\xB0\x8F\xE7\x8B\x97...' for column 'name' at row 1
    * 数据库/表/列的字符编码
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().
      //master("spark://hadoop000:7077")
      master("local")
      .appName("Spark SQL jdbc")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    //import spark.implicits._

    // 查询
    val jdbcDF = spark.read.format("jdbc").option("url", "jdbc:mysql://hadoop000:3306/spark") //数据库名
      .option("driver", "com.mysql.jdbc.Driver").option("dbtable", "student") //表名
      .option("user", "root").option("password", "root").load()
    jdbcDF.show()

    println("**********************************")
    // case class
    jdbcDF.createOrReplaceTempView("student")
    spark.sql("select * from student where age > 1 ").show()

    val connectionProperties = new Properties()
    connectionProperties.put("user", "root")
    connectionProperties.put("password", "root")

    val studentRDD = spark.sparkContext.parallelize(Array("Gabriel 50", "Gabriella 30"))
      .map(x => x.split(" "))
    val ROWRDD = studentRDD.map(x => Row(x(0), x(1).trim.toInt))
    ROWRDD.foreach(print)

    //设置schema
    val schema = StructType(List(StructField("name", StringType, true), StructField("age", IntegerType, true)))

    val studentDF = spark.createDataFrame(ROWRDD, schema)

    studentDF.show()

    println("**********************************")

    // 插入到数据库
    val parameter = new Properties()
    parameter.put("user", "root")
    parameter.put("password", "root")
    parameter.put("driver", "com.mysql.jdbc.Driver")
    studentDF.write.mode("append").jdbc("jdbc:mysql://hadoop000:3306/spark", "student", parameter)
    jdbcDF.show()
  }

}
