package com.atguigu.mlib

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 决策树
  */
object DecisionTreeApp extends App{
  //屏蔽日志
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

  //创建SparkContext
  val conf = new SparkConf().setMaster("local[4]").setAppName("DecisionTree")
  val sc = new SparkContext(conf)

  val path = "Spark-Learning/src/resources/ml/decision_tree/data.txt"
  //加载数据文件
  val data = MLUtils.loadLibSVMFile(sc, path)
  //将数据集切分为70%的训练数据集和30%的测试数据集
  val splits = data.randomSplit(Array(0.7, 0.3))
  val (trainingData, testData) = (splits(0), splits(1))

  //训练决策树模型
  //Empty categoricalFeaturesInfo indicates all features are continuous.
  val numClasses = 2
  val categoricalFeaturesInfo = Map[Int, Int]()
  val impurity = "gini"
  val maxDepth = 5
  val maxBins = 32

  val model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
    impurity, maxDepth, maxBins)

  //评估模型
  val labelAndPreds = testData.map { point =>
    val prediction = model.predict(point.features)
    (point.label, prediction)
  }

  val testErr = labelAndPreds.filter(r => r._1 != r._2).count().toDouble / testData.count()
  println("分类错误度 = " + testErr)
  println("训练的决策树模型:\n" + model.toDebugString)

  //保存决策树模型
  model.save(sc, "target/tmp/myDecisionTreeClassificationModel")
  //重新读取决策树模型
  val sameModel = DecisionTreeModel.load(sc, "target/tmp/myDecisionTreeClassificationModel")

  sc.stop()

}
