package com.atguigu.sparksql.datasource

import org.apache.spark.sql.SparkSession

/**
  * SparkSQL数据源之JSON
  */
object JSON_DF {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().
      //master("spark://hadoop000:7077")
      master("local")
      .appName("Spark SQL jdbc")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // A JSON dataset is pointed to by path.
    // The path can be either a single text file or a directory storing text files
    val path = "D:/workspace/SpringBoot-Learning/Spark-Learning/src/resources/people.json"
    val peopleDF = spark.read.json(path)

    // The inferred schema can be visualized using the printSchema() method
    peopleDF.printSchema()
    // root
    //  |-- age: long (nullable = true)
    //  |-- name: string (nullable = true)

    // Creates a temporary view using the DataFrame
    peopleDF.createOrReplaceTempView("people")

    // SQL statements can be run by using the sql methods provided by spark
    val teenagerNamesDF = spark.sql("SELECT name FROM people WHERE age BETWEEN 13 AND 19")
    teenagerNamesDF.show()
    // +------+
    // |  name|
    // +------+
    // |Justin|
    // +------+

    import spark.implicits._

    // Alternatively, a DataFrame can be created for a JSON dataset represented by
    // a Dataset[String] storing one JSON object per string
    val otherPeopleDataset = spark.createDataset(
      """{"name":"Yin","address":{"city":"Columbus","state":"Ohio"}}""" :: Nil)
    val otherPeople = spark.read.json(otherPeopleDataset)
    otherPeople.show()
    // +---------------+----+
    // |        address|name|
    // +---------------+----+
    // |[Columbus,Ohio]| Yin|
    // +---------------+----+
  }
}
