package com.atguigu.structured

import org.apache.spark.sql.SparkSession

/**
  * StructuredStreaming+kafka
  */
object StructStreamingKafka {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("StructStreamingKafka").master("local[2]").getOrCreate()


    val lines = spark.
      readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
      .option("subscribe", "topic1")
      .option("topic", "updates")
      // 控制断点续传一个batch最大处理条数，避免一次处理太多任务导致内存溢出
      .option("maxOffsetsPerTrigger", "100")
      .load()





    // 隐式转换（DataFrame转DataSet）
    import spark.implicits._
    //行转换成一个个单词
    // Split the lines into words
    val words = lines.as[String].flatMap(_.split(" "))
    //按单词计算词频
    // Generate running word count
    val wordCounts = words.groupBy("value").count()


    // Start running the query that prints the running counts to the console
    val query = wordCounts.writeStream
      // 输出的模式
      .outputMode("complete")

      // 检查点
      //.option("checkpointLocation", "path/to/checkpoint/dir")

      // 输出的类型
      //.option("path", "path/to/destination/dir")
      .format("console")
      .start()

    query.awaitTermination()
  }
}
