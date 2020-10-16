package com.atguigu.java;

/**
 * JEP 360:Sealed Classes(Preview)密封的类和接口（预览）
 * 用于限制超类的使用，密封的类和接口限制其它可能继承或实现它们的其它类或接口。
 * 允许类或接口的开发者来控制哪些代码负责实现，提供了比限制使用超类的访问修饰符声明方式更多选择,并通过支持对模式的详尽分析而支持模式匹配的未来发展。
 * @author shkstart  Email:shkstart@126.com
 * @create 2020 11:28
 */
public sealed class Person permits Teacher,Student,Worker{ } //人

final class Teacher extends Person { }//教师

sealed class Student extends Person permits MiddleSchoolStudent,GraduateStudent{ } //学生

final class MiddleSchoolStudent extends Student { }  //中学生

final class GraduateStudent extends Student { }  //研究生

non-sealed class Worker extends Person { }  //工人

class RailWayWorker extends Worker{} //铁路工人

//class Doctor extends Person{}


