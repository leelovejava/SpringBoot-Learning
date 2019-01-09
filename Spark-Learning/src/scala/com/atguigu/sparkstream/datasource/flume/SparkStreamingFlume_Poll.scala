package com.atguigu.sparkstream.datasource.flume

import java.net.InetSocketAddress

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.flume.{FlumeUtils, SparkFlumeEvent}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming数据源之flume的poll
  */
class SparkStreamingFlume_Poll {

  def main(args: Array[String]) {
    //1、创建sparkConf
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("SparkStreamingFlume_Poll")
    //2、创建sparkContext

    //3、创建StreamingContext
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val address = Seq(new InetSocketAddress("192.168.216.120", 9999), new InetSocketAddress("192.168.216.121", 9999))
    //4、获取flume中数据
    // 在Scala中使用FlumeUtils 读取自定义数据池
    val stream: ReceiverInputDStream[SparkFlumeEvent] = FlumeUtils.createPollingStream(ssc, address, StorageLevel.MEMORY_AND_DISK_SER_2)
    //5、从Dstream中获取flume中的数据  {"header":xxxxx   "body":xxxxxx}
    val lineDstream: DStream[String] = stream.map(x => new String(x.event.getBody.array()))
    //6、切分每一行,每个单词计为1
    val wordAndOne: DStream[(String, Int)] = lineDstream.flatMap(_.split(" ")).map((_, 1))
    //7、相同单词出现的次数累加
    val result: DStream[(String, Int)] = wordAndOne.reduceByKey(_ + _)
    //8、打印输出
    result.print()
    //开启计算
    ssc.start()
    ssc.awaitTermination()
  }
}
