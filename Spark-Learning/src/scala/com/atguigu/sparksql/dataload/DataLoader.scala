package com.atguigu.sparksql.dataload

import java.net.InetAddress

import com.atguigu.sparksql.dataload.util.PropertiesScalaUtils
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.slf4j.LoggerFactory

/**
  * 数据加载 es mongodb
  * 错误:elasticsearch 9300 无法访问,解决:elasticsearch.yml 增加配置
  * # 设置节点之间交互的tcp端口，默认是9300
  * transport.tcp.port: 9300
  */
object DataLoader {

  val logger = LoggerFactory.getLogger(DataLoader.getClass)

  // Moive在MongoDB中的Collection名称【表】
  val MOVIES_COLLECTION_NAME = "Movie"

  // Rating在MongoDB中的Collection名称【表】
  val RATINGS_COLLECTION_NAME = "Rating"

  // Tag在MongoDB中的Collection名称【表】
  val TAGS_COLLECTION_NAME = "Tag"

  // Movie在ElasticSearch中的Index名称
  val ES_MOVIE_TYPE_NAME = "Movie"

  // Tag在ElasticSearch中的Index名称
  //val ES_TAG_TYPE_NAME = "Tag"

  // 配置主机名:端口号的正则表达式
  val ES_HOST_PORT_REGEX = "(.+):(\\d+)".r

  /**
    * Store Data In ElasticSearch
    *
    * @param movies 电影数据集
    * @param esConf ElasticSearch的配置对象
    */
  private def storeMoviesDataInES(movies: DataFrame)(implicit esConf: ESConfig): Unit = {

    // 需要操作的Index名称
    val indexName = esConf.index

    // 新建一个到ES的连接配置
    // 集群名
    val settings: Settings = Settings.builder().put("cluster.name", esConf.clusterName).build()

    // 创建到ES的连接客户端
    val esClient = new PreBuiltTransportClient(settings)

    //对于设定的多个Node分别通过正则表达式进行模式匹配，并添加到客户端实例
    esConf.transportHosts.split(";")
      .foreach {
        case ES_HOST_PORT_REGEX(host: String, port: String) =>
          esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port.toInt))
      }

    // 检查如果Index存在，那么删除Index
    if (esClient.admin().indices().exists(new IndicesExistsRequest(indexName)).actionGet().isExists) {
      // 删除Index
      esClient.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet()
    }
    // 创建Index
    esClient.admin().indices().create(new CreateIndexRequest(indexName)).actionGet()

    // 声明写出时的ES配置信息
    val movieOptions = Map("es.nodes" -> esConf.httpHosts,
      "es.http.timeout" -> "100m",
      "es.mapping.id" -> "mid")

    // 电影数据写出时的Type名称【表】
    val movieTypeName = s"$indexName/$ES_MOVIE_TYPE_NAME"

    // 标签数据写出时的Type名称【表】
    //val tagTypeName = s"$indexName/$ES_TAG_TYPE_NAME"

    // 将Movie信息保存到ES
    movies
      .write.options(movieOptions)
      .mode("overwrite")
      .format("org.elasticsearch.spark.sql")
      .save(movieTypeName)

  }

  /**
    * Store Data In MongoDB
    *
    * @param movies    电影数据集
    * @param ratings   评分数据集
    * @param tags      标签数据集
    * @param mongoConf MongoDB的配置
    */
  private def storeDataInMongo(movies: DataFrame, ratings: DataFrame, tags: DataFrame)(implicit mongoConf: MongoConfig): Unit = {

    // 1) 创建到MongoDB的连接
    val mongoClient = MongoClient(MongoClientURI(mongoConf.uri))

    // collection table 数据库表/集合

    // 2) 删除Movie的Collection
    mongoClient(mongoConf.db)(MOVIES_COLLECTION_NAME).dropCollection()

    // 删除Rating的Collection
    mongoClient(mongoConf.db)(RATINGS_COLLECTION_NAME).dropCollection()

    // 删除Tag的Collection
    mongoClient(mongoConf.db)(TAGS_COLLECTION_NAME).dropCollection()

    // 3) Spark SQL提供的write方法进行数据的分布式插入
    // 将Movie数据集写入到MongoDB
    movies
      .write
      .option("uri", mongoConf.uri)
      .option("collection", MOVIES_COLLECTION_NAME)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    // 将Rating数据集写入到MongoDB
    ratings
      .write.option("uri", mongoConf.uri)
      .option("collection", RATINGS_COLLECTION_NAME)
      // 模式:覆盖
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    // 将Tag数据集写入到MongoDB
    tags
      .write.option("uri", mongoConf.uri)
      .option("collection", TAGS_COLLECTION_NAME)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    // 4) 创建索引
    mongoClient(mongoConf.db)(MOVIES_COLLECTION_NAME).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConf.db)(RATINGS_COLLECTION_NAME).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConf.db)(RATINGS_COLLECTION_NAME).createIndex(MongoDBObject("uid" -> 1))
    mongoClient(mongoConf.db)(TAGS_COLLECTION_NAME).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConf.db)(TAGS_COLLECTION_NAME).createIndex(MongoDBObject("uid" -> 1))

    // 5) 关闭MongoDB的连接
    mongoClient.close()
  }

  def main(args: Array[String]): Unit = {

    // [mid,name,descri,timelong,issue,shoot,language,genres,actors,directors]
    val DATAFILE_MOVIES = "D:\\workspace\\data\\RecommendSystem\\movies.csv"

    // [userId,movieId,rating,timestamp]
    val DATAFILE_RATINGS = "D:\\workspace\\data\\RecommendSystem\\ratings.csv"

    // [userId,movieId,tag,timestamp]
    val DATAFILE_TAGS = "D:\\workspace\\data\\RecommendSystem\\tags.csv"

    /**
      * mutable和immutable
      * Scala collections systematically distinguish between mutable and immutable collections. A mutable collection can be updated or extended in place.
      * This means you can change, add, or remove elements of a collection as a side effect. Immutable collections, by contrast, never change. You have still operations that simulate additions, removals, or updates,
      * but those operations will in each case return a new collection and leave the old collection unchanged.
      * scala中的集合分为两种，一种是可变的集合，另一种是不可变的集合
      * 可变的集合可以更新或修改，添加、删除、修改元素将作用于原集合
      * 不可变集合一量被创建，便不能被改变，添加、删除、更新操作返回的是新的集合，老集合保持不变
      */
    // 创建全局配置
    /* val params = scala.collection.mutable.Map[String, Any]()
     params += "spark.cores" -> "local[*]"
     params += "mongo.uri" -> "mongodb://hadoop000:27017/recom3"
     params += "mongo.db" -> "recom3"
     params += "es.httpHosts" -> "localhost:9200"
     params += "es.index" -> "recom3"
     params += "es.transportHosts" -> "localhost:9300"
     params += "es.cluster.name" -> "es-cluster"*/

    //params.put("spark.cores", "local[*]")


    // 声明Spark的配置信息
    val conf = new SparkConf().setAppName("Dataloader").setMaster(config.params("spark.cores"))

    // 创建SparkSession
    val spark = SparkSession.builder()
      .config(conf)
      .getOrCreate()

    // 引入SparkSession内部的隐式转换
    import spark.implicits._

    // 定义MongoDB的配置对象
    implicit val mongoConf = new MongoConfig(config.params("mongo.uri").asInstanceOf[String], config.params("mongo.db").asInstanceOf[String])

    // 定义ElasticSearch的配置对象
    implicit val esConf = new ESConfig(config.params("es.httpHosts").asInstanceOf[String], config.params("es.transportHosts").asInstanceOf[String], config.params("es.index").asInstanceOf[String], config.params("es.cluster.name").asInstanceOf[String])

    // 加载Movie数据集
    val movieRDD = spark.sparkContext.textFile(DATAFILE_MOVIES)

    // 加载Rating数据集
    val ratingRDD = spark.sparkContext.textFile(DATAFILE_RATINGS)

    // 加载Tag数据集
    val tagRDD = spark.sparkContext.textFile(DATAFILE_TAGS)

    // 将电影RDD转换为DataFrame
    val movieDF = movieRDD.map(line => {
      val x = line.split("\\^")
      //Movie(x(0).trim.toInt, x(1).trim, x(2).trim, x(3).trim, x(4).trim, x(5).trim, x(6).trim.split("\\|"), x(7).trim.split("\\|"), x(8).trim.split("\\|"), x(9).trim.split("\\|"))
      Movie(x(0).trim.toInt, x(1).trim, x(2).trim, x(3).trim, x(4).trim, x(5).trim, x(6).trim, x(7).trim, x(8).trim, x(9).trim)
    }).toDF()

    // 将评分RDD转换为DataFrame
    val ratingDF = ratingRDD.map(line => {
      val x = line.split(",")
      Rating(x(0).toInt, x(1).toInt, x(2).toDouble, x(3).toInt)
    }).toDF()


    // 将标签RDD转换为DataFrame
    val tagDF = tagRDD.map(line => {
      val x = line.split(",")
      Tag(x(0).toInt, x(1).toInt, x(2), x(3).toInt)
    }).toDF()
    // 1240597180
    // 1297398822
    // 缓存
    movieDF.cache()
    tagDF.cache()

    // 引入内置函数库
    import org.apache.spark.sql.functions._

    //将tagDF中的标签合并在一起
    val tagCollectDF = tagDF.groupBy($"mid").agg(concat_ws("|", collect_set($"tag")).as("tags"))

    //将tags合并到movie数据集中产生新的movie数据集
    val esMovieDF = movieDF.join(tagCollectDF, Seq("mid", "mid"), "left").select("mid", "name", "descri", "timelong", "issue", "shoot", "language", "genres", "actors", "directors", "tags")

    // 将数据保存到ES
    storeMoviesDataInES(esMovieDF)

    // 将数据保存到MongoDB
    storeDataInMongo(movieDF, ratingDF, tagDF)

    // 去除缓存
    tagDF.unpersist()
    movieDF.unpersist()

    // 关闭Spark
    spark.close()

  }
}

object config {
  val params = Map(
    "spark.cores" -> PropertiesScalaUtils.loadProperties("spark.cores"),
    "mongo.uri" -> PropertiesScalaUtils.loadProperties("mongo.uri"),
    "mongo.db" -> PropertiesScalaUtils.loadProperties("mongo.db"),
    "es.httpHosts" -> PropertiesScalaUtils.loadProperties("es.httpHosts"),
    "es.index" -> PropertiesScalaUtils.loadProperties("es.index"),
    "es.transportHosts" -> PropertiesScalaUtils.loadProperties("es.transportHosts"),
    "es.cluster.name" -> PropertiesScalaUtils.loadProperties("es.cluster.name"),
    "mongo.collection.Rating" -> "Rating",
    "mongo.collection.Movie"-> "Movie"
  )
}
