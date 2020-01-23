/**
  * 定义最简单的类，并完成实例化
  * java.lang.Object是JVM中所有实例的根，包括scala。
  * 打印一个实例时，会调用该实例的toString方法，这是它从根类型继承的方法。
  */
class User
val u = new User
val isAnyRef = u.isInstanceOf[AnyRef]

/**
  * 增加值和方法
  */
class User1 {
  val name:String = "Yubaba"
  def greet:String = s"Hello from $name"
  override def toString: String = s"User($name)"
}
val u1 = new User1
println(u1.greet)

/**
  * 将name参数化。
  * 类参数可以用来初始化字段（类中的值和变量），或者用于传入函数，但是一旦类已经创建，这些参数就不再可用。
  */
class User2(n:String) {
  val name:String = n
  def greet:String = s"Hello from $name"
  override def toString = s"User($name)"
}
val u2 = new User2("Zeniba")
println(u2.greet)

/**
  * 除了使用类参数来完成初始化，还可以把某个字段声明为类参数。
  * 通过在类参数前增加关键字val或var，类参数就成为类中的一个字段。
  */
class User3(val name:String){
  def greet:String = s"Hello from $name"
  override def toString = s"User($name)"
}
val users = List(new User3("Shoto"),new User3("Art3mis"),new User3("Aesch"))
val sizes = users map(_.name.size)
val sorted = users sortBy(_.name)
val third = users find (_.name contains("3"))
val greet = third map (_.greet) getOrElse("hi")

/**
  * 在scala中，使用extends扩展最多一个其他类，用override覆盖所继承方法的行为。
  * 类中的字段和方法可以用this关键字访问（如果确实必要），父类中的字段和方法可以用super关键字访问。
  */
class A {
  def hi = "Hello from A"
  override def toString = getClass.getName
}
class B extends A
class C extends B { override def hi = "hi C -> " + super.hi }
val hiA = new A().hi
val hiB = new B().hi
val hic = new C().hi

















