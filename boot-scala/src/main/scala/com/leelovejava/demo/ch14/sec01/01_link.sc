class A {
  private var name:String=null
  /*def setName(name:String)={
    this.name = name
    this // 返回调用对象
  }*/

  def setName(name:String):this.type ={
    this.name = name
    this // 返回调用对象
  }
}
class B extends A{
  private var sex:String = null
  def setAge(sex:String)={
    this.sex=sex
    this
  }
}
val a:A = new A
val b:B = new B
b.setName("WangBa").setAge("woman")  // 无法执行
print(b)
b.setAge("WangBa").setName("woman")  // 可以执行
print(b)


