package com.atguigu.sparksql.dataload

/**
  * MongoDB 配置对象
  *
  * @param uri MongoDB连接地址
  * @param db  操作的MongoDB数据库
  */
case class MongoConfig(val uri: String, val db: String)

/**
  * ElasticSearch 配置对象
  *
  * @param httpHosts      ES的地址
  * @param transportHosts ES的通讯端口
  * @param index          ES操作的Index
  * @param clusterName    ES的集群名称
  */
case class ESConfig(val httpHosts: String, val transportHosts: String, val index: String, val clusterName: String)

/**
  * Movie Class 电影类
  *
  * @param mid       电影的ID
  * @param name      电影的名称
  * @param descri    电影的描述
  * @param timelong  电影的时长
  * @param issue     电影的发行时间
  * @param shoot     电影的拍摄时间
  * @param language  电影的语言
  * @param genres    电影的类别
  * @param actors    电影的演员
  * @param directors 电影的导演
  */
//case class Movie(val mid: Int, val name: String, val descri: String, val timelong: String, val issue: String, val shoot: String, val language: Array[String], val genres: Array[String], val actors: Array[String], val directors: Array[String])
case class Movie(val mid: Int, val name: String, val descri: String, val timelong: String, val issue: String, val shoot: String, val language: String, val genres: String, val actors: String, val directors: String)

/**
  * Tag Class  电影标签类
  *
  * @param uid       用户的ID
  * @param mid       电影的ID
  * @param tag       用户为该电影打的标签
  * @param timestamp 用户为该电影打标签的时间
  */
case class Tag(val uid: Int, val mid: Int, val tag: String, val timestamp: Int)

/**
  * Rating Class 电影的评分类
  *
  * @param uid       用户的ID
  * @param mid       电影的ID
  * @param score     用户为该电影的评分
  * @param timestamp 用户为该电影评分的时间
  */
case class Rating(val uid: Int, val mid: Int, val score: Double, val timestamp: Int)

/**
  * Rating数据集，用户对于电影的评分数据集，用，分割
  *
  * 1,           用户的ID
  * 31,          电影的ID
  * 2.5,         用户对于电影的评分
  * 1260759144   用户对于电影评分的时间
  */
case class MovieRating(val uid: Int, val mid: Int, val score: Double, val timestamp: Int)


