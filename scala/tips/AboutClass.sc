/**
  * 1、定义最简单的类，并完成实例化
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


/**
 * 基本的类定义：
 *   class <identifier> [extends <identifier>] [{fields,methods,and classes}]
 * 类可以相互嵌套，嵌套类可以访问父类的字段和方法。
 * 类可以有类参数(class parameters)，这是初始化类所用的字段和方法的输入值，或者甚至可以作为类的字段。
 * 定义有输入参数的类：
 *   class <identifier> ([val|var] <identifier>:<type>[, ... ])
 *                       [extends <identifier>(<input parameters>)]
 *                       [{ fields and methods }]
 */
class Car(val make:String,var reserved:Boolean){
  def reserve(r:Boolean):Unit = {reserved = r}
}
val t = new Car("Toyota",false)
t.reserve(true)
println(s"My ${t.make} is now reserved? ${t.reserved}")

val t2 = new Car(reserved = false,make = "Tesla")
println(t2.make)

class Lotus(val color:String,reserved:Boolean) extends Car("Lotus",reserved)
val l = new Lotus("Silver",false)
println(s"Requested a ${l.color} ${l.make}")

/**
 * 除了输入参数，类参数还从函数借用了另一个特性，可以为参数定义默认值。
 *   class <identifier> ([val|var] <identifier>:<type> = <expression>[, ... ])
 *                       [extends <identifier>(<input parameters>)]
 *                       [{ fields and methods }]
 */
class Car1(val make:String,var reserved:Boolean=true,val year:Int = 2015){
  override def toString = s"$year $make,reserved = $reserved"
}
val a1 = new Car1("Acura")
val l1 = new Car1("Lexus",year = 2010)
val p1 = new Car1(reserved = false,make = "Porsche")

/**
 * 定义一个有类型参数的类：
 *   class <identifier> [type:parameters]
 *                       ([val|var] <identifier>:<type> = <expression>[, ... ])
 *                       [extends <identifier>[type-parameters](<input parameters>)]
 *                       [{ fields and methods }]
 */
class Singular[A](element:A) extends Traversable[A] {
  def foreach[B](f:A => B) = f(element)
}
val p = new Singular("Planes")
p foreach println
val name:String = p.head


/**
 * 抽象(abstract class)是将由其他类扩展的一个类，而自己不能实例化。
 */
abstract class Car2 {
  val year:Int
  val automatic:Boolean = true
  def color:String
}
class RedMini(val year:Int) extends Car2{
  def color = "Red"
}
val m:Car2 = new RedMini(2005)

class Mini(val year:Int,val color:String) extends Car2
val redMini:Car2 = new Mini(2005,"Red")
println(s"Got a ${redMini.color} Mini")

/**
 * 匿名类
 */
abstract class Listener { def trigger }
val myListener = new Listener {
  override def trigger: Unit = {println(s"Trigger at ${new java.util.Date}")}
}
myListener.trigger

class Listening {
  var listener:Listener = null
  def register(l:Listener) { listener = l }
  def sendNotification() { listener.trigger }
}
val notification = new Listening
notification.register(new Listener {
  override def trigger: Unit = println(s"Trigger at ${new java.util.Date()}")
})
notification.sendNotification()

/**
 * 重载方法时为调用者提供更多选择的一种策略。
 */
class Printer(msg:String){
  def print(s:String):Unit = println(s"$msg:$s")
  def print(l:Seq[String]):Unit = print(l.mkString(", "))
}
new Printer("Today`s Report").print("Foggy" :: "Rainy" :: "Hot" :: Nil)

/**
 * apply方法有时是指它要作为一个默认方法或一个注入方法(injector method)，可以直接调用而不需要方法名。
 */
class Multiplier(factor:Int){
  def apply(input:Int) = input * factor
}
val tripleMe = new Multiplier(3)
val tripled = tripleMe.apply(10)
val tripled2 = tripleMe(10)

/**
 * 懒值时介于常规类值和方法之间的一种机制。
 * 初始化一个常规类值所用的表达式只是在实例化时执行一次；
 * 构成一个方法的表达式则有所不同，它在每次调用这个方法时都会执行；
 * 初始化一个懒值的表达式会在调用这个值时执行，但只是第一次。可将其看成是一个缓存的函数结果。
 *
 * 可在val关键字前加上lazy关键字来创建一个懒值。
 */
class RandomPoint{
  val x = {println("creating x");util.Random.nextInt()}
  lazy val y = {println("now y");util.Random.nextInt()}
}
val p2 = new RandomPoint
println(s"Location is ${p2.x},${p2.y}")


/**
 * 利用包，可以按目录使用点分隔路径来组织Scala代码。
 * 在Scala源文件最前面使用package关键字，则声明这个文件中所有类都将包含在这个包中。
 *   package <identifier>
 * Scala源文件要存储在与包匹配的目录中。
 *
 * 可以用完全点分隔包路径和类名来访问包装类。
 * 与java中不同，scala代码中能使用语句的任何位置都可以使用import。
 */
import java.util.Date

import scala.collection.mutable
val d = new Date()

println("Your new UUID is "+{import java.util.UUID;UUID.randomUUID()})

/**
 * scala支持用下划线操作符导入一个包的全部内容。
 */
import scala.collection.mutable._
val b = new ArrayBuffer[String]()
b += "Hello"
val q = new Queue[Int]()
q.enqueue(3,4,5)
val pop = q.dequeue()
println(q)

/**
 * scala在每个Scala类中完成自己的自动导入，导入整个scala._和java.lang._包。
 * 因此Scala的随机工具类位于scala.util.Random，不过完全可以作为util.Random来访问。
 * 类似的java.lang.Thread完全可以按其类名来访问。
 *
 * 导入组：
 *   import <package>.{<class 1>[,<class 2> ...]}
 */
import scala.collection.mutable.{Queue,ArrayBuffer}
val q1 = new Queue[Int]
val b1 = new ArrayBuffer[String]()

/**
 * 导入别名：
 *   import <package>.{<original name> => <alias>}
 */
import scala.collection.mutable.{Map => MutMap}
val m2 = MutMap(1 -> 2)
m2.remove(1);println(m2)

/**
 * 包装语法(packaging syntax)会将包块中的类指定为这个包的成员。
 * 这样同一个文件就可以包含不同包的成员，另外还能在类似REPL的非文件环境中清晰地区分不同的包。
 *   package <identifier> { <class definitions> }
 *
 *   package com {
 *     package oreilly {
 *       class Config(val baseUrl:String = "http://localhost")
 *     }
 *   }
 */


/**
 * 默认地，Scala不会增加私密性控制。你写的任何类都可以实例化，任何代码都可以访问类的字段和方法。
 * 一种私密性控制是将字段和方法标志为受保护的(protected)，限制只有同一个类或其子类中的代码才能访问到这个字段或方法。
 * 需要更严格的保护时，可以标志字段和方法为private，仅限定义这个字段或方法的类可以访问。
 * 除了指定private或protected，还可以通过指定访问修饰符增加这一级控制。
 * 访问修饰符指定相应限定只在某个给定点以上有效，如一个包、类或实例，而在这个点以内无效。
 *   private|protected [xxx]
 */

/**
 * 最终类：
 *   final类成员不能在子类中被覆盖，也可把整个类标志为final，避免其他类派生这个类。
 *
 * 密封(sealed)类：
 *   密封类会限制一个类的子类必须位于父类所在的同一个文件中。
 */




