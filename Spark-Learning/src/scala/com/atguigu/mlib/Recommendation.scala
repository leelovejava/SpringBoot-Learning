/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package com.atguigu.mlib

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating

object RecommendationExample {
  def main(args: Array[String]): Unit = {
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    //创建SparkContext
    val conf = new SparkConf().setMaster("local[4]").setAppName("CollaborativeFiltering")
    val sc = new SparkContext(conf)

    //加载数据
    val path = "Spark-Learning/src/resources/ml/collaborative_filtering/test.data"
    val data = sc.textFile(path)
    val ratings = data.map(_.split(',') match { case Array(user, item, rate) =>
      Rating(user.toInt, item.toInt, rate.toDouble)
    })

    //训练模型
    val rank = 10
    val numIterations = 10
    // ALS(Alternating Least Square)交替最小二乘算法
    val model = ALS.train(ratings, rank, numIterations, 0.01)

    //准备用户数据
    val usersProducts = ratings.map { case Rating(user, product, rate) =>
      (user, product)
    }

    //生成预测结果
    val predictions =
      model.predict(usersProducts).map { case Rating(user, product, rate) =>
        ((user, product), rate)
      }

    //对比结果
    val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(predictions)

    //生成均方误差
    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
      println(s"【用户】：${user}  【物品】：${product}  【真实值】：${r1}  【预测值】：${r2}")
      val err = (r1 - r2)
      err * err
    }.mean()
    println("预测的均方误差为 = " + MSE)

    //保存模型
    model.save(sc, "target/tmp/myCollaborativeFilter")
    //加载模型
    val sameModel = MatrixFactorizationModel.load(sc, "target/tmp/myCollaborativeFilter")

    sc.stop()
  }
}
