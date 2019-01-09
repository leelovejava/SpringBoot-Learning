package com.atguigu.structured

import org.apache.spark.sql.SparkSession

/**
  * 结构化流的worldCount
  *
  * @author tianhao
  * @date 2019-01-05
  */
object SqlNetworkWordCount {


  def main(args: Array[String]): Unit = {
    //创建Spark SQL切入点
    val spark = SparkSession.builder().appName("SqlNetworkWordCount").master("local[2]").getOrCreate()
    //读取服务器端口发来的行数据，格式是DataFrame
    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    val lines = spark.readStream
      // 配置输出的格式
      .format("socket")
      .option("host", "hadoop001")
      .option("port", 9999)
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
    // query name 查询的名称，类似tempview的名字
    // trigger interval：触发的间隔时间，如果前一个batch处理超时了，那么不会立即执行下一个batch，而是等下一个trigger时间在执行
    // checkpoint location：为保证数据的可靠性，可以设置检查点保存输出的结果

    // +--------------------+-----+
    // |               value|count|
    // +--------------------+-----+
    // |      ssssssssssssss|    3|
    // |       sssssssssssss|    1|
    // |              111111|    2|
    // |   sssssssssssssssss|    1|
    // +--------------------+-----+
    query.awaitTermination()

    // spark-submit --class com.atguigu.structured.WorldCount  --master yarn structuredstreaming_wordCount.jar
  }
}
