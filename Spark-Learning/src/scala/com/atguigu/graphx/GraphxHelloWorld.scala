package com.atguigu.graphx

import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

/**
  * Spark图计算 helloWorld
  * Created by wuyufei on 10/09/2017.
  */
object GraphxHelloWorld {

  val logger = LoggerFactory.getLogger(GraphxHelloWorld.getClass)

  def main(args: Array[String]) {


    //创建SparkConf()并设置App名称
    val conf = new SparkConf().setMaster("local[3]").setAppName("WC")
    //创建SparkContext，该对象是提交spark App的入口
    val sc = new SparkContext(conf)

    // Create an RDD for the vertices
    val users: RDD[(VertexId, (String, String))] =
      sc.parallelize(Array((3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")),
        (5L, ("franklin", "prof")), (2L, ("istoica", "prof"))))
    // Create an RDD for edges
    val relationships: RDD[Edge[String]] =
      sc.parallelize(Array(Edge(3L, 7L, "collab"), Edge(5L, 3L, "advisor"),
        Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi")))

    val defaultUser = ("John Doe", "Missing")
    // Build the initial Graph
    val graph = Graph(users, relationships, defaultUser)

    // Count all users which are postdocs
    val verticesCount = graph.vertices.filter { case (id, (name, pos)) => pos == "postdoc" }.count
    println(verticesCount)

    // Count all the edges where src > dst
    val edgeCount = graph.edges.filter(e => e.srcId > e.dstId).count
    println(edgeCount)

    sc.stop()


  }

}




