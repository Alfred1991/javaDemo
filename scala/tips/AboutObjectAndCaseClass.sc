/**
 * 对象(object)是一个类类型，只能有不超过一个实例，在面向对象设计中称为一个单例(singleton)。
 * 对象不是用new关键字创建实例，只需要按名直接访问对象。对象会在首次访问时在JVM中自动实例化。
 * Java和其他语言可以指定一个类的某些字段和方法为"静态"或"全局"，scala的对象提供了类似的功能。
 * 对象和类没有完全解耦合，对象可以扩展另一个类，从而可以在一个全局实例中使用它的字段和方法。不过反过来不成立，因为对象本身不能扩展。
 * 定义对象要使用object关键字，对象没有任何参数，不过可以像定义常规类一样为对象定义字段、方法和内部类。
 * 定义对象：
 *   object <identifier> [extends <identifier>] [{fields,methods, and classes}]
 */
object Hello { println("in Hello");def hi = "hi" }
println(Hello.hi)
println(Hello.hi)

/**
 * 最适合对象的方法是纯函数和处理外部IO的函数。
 */
object HtmlUtils {
  def removeMarkup(input:String) = {
    input.replaceAll("""</?\w[^>]*>""","").replaceAll("<.*>","")
  }
}
val html = "<html><body><h1>Introduction</h1></body></html>"
val text = HtmlUtils.removeMarkup(html)

/**
 * 类的apply方法也适用于对象。
 * Future对象也有apply()方法，它取一个函数参数，并在一个后台线程中调用该函数。
 * 这在面向对象编程中称为工厂模式，这也是对象apply()方法的一种流行用法。
 * 具体来讲，工厂模式是一种很流行的方法，可以从伴生对象生成一个类的新实例。
 * 伴生对象(companion object)是与类同名的一个对象，与类在同一个文件中定义。
 * 为类提供一个伴生对象在scala中是一个常用的模式，另外还可以由此得到一个特性。
 * 从访问控制的角度讲，伴生对象和类可以认为是单个单元，所以它们可以相互访问私有和保护字段及方法。
 */
class Multiplier(val x:Int) { def product(y:Int) = x * y }
object Multiplier{ def apply(x:Int) = new Multiplier(x)}
val tripler = Multiplier(3)
val result = tripler.product(13)

/**
 * 伴生对象带来的好处是可以与伴生类共享特殊的访问控制。
 * 类会访问其伴生对象的私有成员。
 */
object DBConncetion {
  private val db_url = "jdbc://localhost"
  private val db_user = "franken"
  private val db_pass = "berry"

  def apply(): DBConncetion = new DBConncetion()
}
class DBConncetion{
  private val props = Map(
    "url" -> DBConncetion.db_url,
    "user" -> DBConncetion.db_user,
    "pass" -> DBConncetion.db_pass,

  )
}

/**
 * 使用对象的命令行应用。
 * scala使用对象中的一个"main"方法作为应用的入口点，它取一个字符串数组作为输入参数。
 *
 * 另一种应用程序入口的方式是继承App trait
 */
object Date {
  def main(args:Array[String]): Unit ={
    println(new java.util.Date)
  }
}

object Cat {
  def main(args: Array[String]): Unit = {
    for(arg <- args){
      println( io.Source.fromFile(arg).mkString )
    }
  }
}

/**
 * 使用case类完成类和对象的交互。
 * case class是不可实例化的类，包含多个自动生成的方法。
 * 它还包括一个自动生成的伴生对象，这个对象也有其自己的自动生成的方法。
 * 类中以及伴生对象中的所有这些方法都建立在类的参数表基础上，这些参数用来构成equals实现和toString等方法。
 *
 * case类对数据传输对象很适用，根据所生成的基于数据的方法，这些类主要用于存储数据。
 * 不过，它们不适合层次类结构，因为继承的字段不能用来构建工具方法。
 * 定义一个case类：
 *   case class <identifier> ([var] <identifier>:<type>[, ... ])
 *                            [extends <identifier>(<input parameters>)]
 *                            [{ fields and methods }]
 * 生成的case类方法：
 *   apply  用于实例化case类的工厂方法。
 *   copy
 *   equals
 *   hashCode
 *   toString
 *   unapply  将实例抽取到一个字段元组，从而可以使用case类实例完成模式匹配。
 * scala编译器会为case类生成方法，不过除了这些方法时自动生成的，实际上它们并没有其他特殊之处。
 */
case class Character(name:String,isThief:Boolean)
//伴生对象的工厂方法
val h = Character("Hadrian",true)
val r = h.copy(name="Royce")
h == r
//利用伴生对象的unapply方法，可以将实例分解为不同部分，绑定第一个字段，并使用一个字面量值匹配第二个字段
h match {
  case Character(x,true) => s"$x is a thief"
  case Character(x,false) => s"$x is not a thief"
}

/**
 * 如果你的case类扩展了另一个类，后者有其自己的字段，
 * 若我们没有将这些字段增加为case类参数，则生成的方法就不能利用这些字段。
 */


/**
 * 使用scala实现工厂方法
 */
trait Animal {
  def speak
}
object Animal {
  private class Dog extends Animal {
    override def speak { println("woof") }
  }
  private class Cat extends Animal {
    override def speak { println("meow") }
  }
  // the factory method
  def apply(s: String): Animal = {
    if (s == "dog")
      new Dog
    else
      new Cat
  }
}
val cat = Animal("cat") // creates a Cat
val dog = Animal("dog") // creates a Dog
cat.speak
dog.speak


