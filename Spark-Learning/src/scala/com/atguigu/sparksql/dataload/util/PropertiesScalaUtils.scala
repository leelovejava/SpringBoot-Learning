package com.atguigu.sparksql.dataload.util

import java.util.Properties

/**
  * 读取properties的属性文件
  */
object PropertiesScalaUtils {
  def loadProperties(key: String): String = {
    val properties = new Properties()
    val in = PropertiesScalaUtils.getClass.getClassLoader.getResourceAsStream("application.properties")
    properties.load(in)
    properties.getProperty(key)
  }
}
