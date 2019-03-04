package com.leelovejava.obj

/**
  * 声明式编程
  */
object DecProgramApp {

  def main(args: Array[String]): Unit = {
    val page = 1
    val allUsers = List(User("jack", 1, "2018-12-12"), User("rose", 2, "2018-12-13"))
    val pageList =
      allUsers
        .sortBy(u => (u.role, u.name, u.addTime)) // 依次按 role, name, addTime 进行排序
        .drop(page * 10) // 跳过之前页数据
        .take(10) // 取当前页数据，如不足10个则全部返回
  }
}

case class User(name: String, role: Int, addTime: String)