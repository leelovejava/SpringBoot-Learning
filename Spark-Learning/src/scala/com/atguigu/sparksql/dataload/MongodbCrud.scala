package com.atguigu.sparksql.dataload

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object MongodbCrud {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster(config.params("spark.cores")).setAppName("MongodbCrud")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    val mongoConfig = MongoConfig(config.params("mongo.uri"), config.params("mongo.db"))
    import spark.implicits._

    //读取mongoDB中的业务数据
    val ratingRDD = spark
      .read
      .option("uri", mongoConfig.uri)
      .option("collection", config.params("mongo.collection.Movie"))
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRating]
      .rdd
      .map(rating =>
        (rating.uid, rating.mid, rating.score)
      )
  }
}
