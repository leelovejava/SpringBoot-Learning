package com.atguigu.sparksql.dataload

import org.apache.spark.sql.DataFrame

object EsCrud {
  /**
    * 获取es中的数据
    * @param movies
    * @param esConf
    */
  private def getMoviesDataInES(movies: DataFrame)(implicit esConf: ESConfig): Unit = {

  }
}
