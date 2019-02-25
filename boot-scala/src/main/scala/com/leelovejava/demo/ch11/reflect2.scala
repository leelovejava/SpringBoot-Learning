package com.leelovejava.demo.ch11

// 反射
    //scala.reflect.api.Mirrors#InstanceMirror
    //scala.reflect.api.Mirrors#MethodMirror
    //scala.reflect.api.Mirrors#FieldMirror
    //scala.reflect.api.Mirrors#ClassMirror
    //scala.reflect.api.Mirrors#ModuleMirror

case class Employee(name:String){
  def showName = print(">>>" + name)
}

//******************  Main  *********************
object Main4 extends App {
  //引入反射类型转换
  /*import scala.reflect.runtime.universe._

  //用于获取实例的类型信息的方法
  def getTypeTag[T:TypeTag](a:T) = typeTag[T]

  val emp = Employee("China")
  val classType = getTypeTag(emp).tpe
  //val classType = typeOf[Employee]

  // 获取运行期镜像
  val runtimeMirrorRef = runtimeMirror(getClass.getClassLoader)

  //获取类镜像，类似于Java中的Class<?>
  val classMirrorRef = runtimeMirrorRef.reflectClass(classType.typeSymbol.asClass)

  //获取class里面定义的构造器方法
  val methodConstructorRef = classType.decl(termNames.CONSTRUCTOR).asMethod

  //获取类实例中定义的构造函数引用
  val constructorRef = classMirrorRef.reflectConstructor(methodConstructorRef)

  //构造对象
  val employee = constructorRef("Mike")

  //获取name属性标识
  val nameTermSymb = classType.decl(TermName("name")).asTerm

  //获取showName方法标识
  val showNameTermSymb = classType.decl(TermName("showName")).asMethod

  //获取运行时的实例引用
  val instanceMirrorRef = runtimeMirrorRef.reflect(employee)

  //获取运行时的属性引用
  val fieldMirrorRef = instanceMirrorRef.reflectField(nameTermSymb)

  //获取运行时的方法引用
  val methodMirrorRef = instanceMirrorRef.reflectMethod(showNameTermSymb)

  //读取属性
  println(fieldMirrorRef.get)

  //调用方法
  methodMirrorRef.apply()*/

}
