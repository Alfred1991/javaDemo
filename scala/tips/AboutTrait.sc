/**
 * 1、Trait是一种支持多重继承的类。trait不能实例化。
 * 类、case类、对象以及trait都只能扩展不超过一个类，但是可以同时扩展多个trait。
 * 类似对象，trait不能有类参数。但与对象不同的是，trait可以有类型参数，这使它们有很好的可重用性。
 * 定义trait：
 *   trait <identifier> [extends <identifier>][{ fields,methods, and classes }]
 */
trait HtmlUtils {
  def removeMarkup(input:String) = {
    input.replaceAll("""</?\w[^>]*>""","").replaceAll("<.*>","")
  }
}
class Page(val s:String) extends HtmlUtils {
  def asPlainText = removeMarkup(s)
}
new Page("<html><body><h1>Introduction</h1></body></html>").asPlainText

/**
 * 增加第二个trait，使用新关键字with，扩展第二个以及更多trait时必须使用该关键字
 */
trait SafeStringUtils {
  def trimToNone(s:String):Option[String] = {
    Option(s) map(_.trim) filterNot(_.isEmpty)
  }
}
class Page1(val s:String) extends SafeStringUtils with HtmlUtils {
  def asPlainText:String = {
    trimToNone(s) map removeMarkup getOrElse "n/a"
  }
}
new Page1("<html><body><h1>Introduction</h1></body></html>").asPlainText
new Page1("  ").asPlainText
new Page1(null).asPlainText

/**
 * 尽管scala理论上支持多重继承，但编译器实际上会创建各个trait的副本，形成类和trait组成的一个"很高"的单列层次体系。
 * 所以，如果一个类扩展了类A以及trait B和C，编译到.class二进制文件时，实际上它会扩展一个类，这个类又扩展了另一个类，后者进一步扩展了下一个类。
 * 这里将所扩展的类和trait的水平列表变换为一个垂直链，各个类分别扩展另一个类，这个过程称为线性化(linearization)。
 * 这是一种复制机制，用于在只支持单重继承的执行环境中支持多重继承。
 * JVM只支持单重继承。
 *
 * 如果一个类导入两个trait，它们有相同的字段或成员，但是没有override关键字，这个类的编译就会失败。
 * 可增加一个公共基类，然后用override关键字覆盖字段和方法，这样可以确保trait能够由同一个类扩展。
 *
 * 2、关于线性化，要理解最重要的一点是scala编译器以什么顺序组织trait和可选的类来相互扩展。多重继的顺序为从右到左。
 * 因此若一个类定义为class D extends A with B with C，其中A是一个类，B和C是trait，
 * 将由编译器重新实现为class D extends C extends B extends A。最右trait是所定义的类的直接父类，这个类或第一个trait成为最后一个类。
 */
trait Base {override def toString: String = "Base"}
class A extends Base {override def toString: String = "A->"+super.toString}
trait B extends Base {override def toString: String = "B->"+super.toString}
trait C extends Base {override def toString: String = "C->"+super.toString}
class D extends A with B with C {override def toString: String = "D->"+super.toString}
new D()

/**
 * 线性化的一个好处是，可以编写trait来覆盖共享父类的行为。
 * 由于trait线性化的顺序是从右到左，所以"Paint"的层次体系是"Paint" -> "Opaque" -> "RGBColor"。
 * 增加到Paint类的类参数用来初始化RGBColor类，而Paint和RGBColor之间的Opaque traint覆盖了hex方法用来增加额外的功能。
 */
class RGBColor(val color:Int){def hex = f"$color%06X"}
val green = new RGBColor( 255 << 8 ).hex
trait Opaque extends RGBColor { override def hex = s"${super.hex}FF" }
trait Sheer extends RGBColor { override def hex = s"${super.hex}33" }
class Paint(color:Int) extends RGBColor(color) with Opaque
class Overlay(color:Int) extends RGBColor(color) with Sheer
val red = new Paint(128 << 16).hex
val blue = new Overlay(192).hex

/**
 * 3、自类型(self type)是一个trait注解，向一个类增加这个trait时，要求这个类必须有一个特定的类型或子类型。
 * 有自类型注解的trait不能增加到未扩展指定类型的类。
 * 在某种程度上，这可以保证trait总是扩展该类型，尽管不是直接扩展。
 * 定义自类型：
 *   trait ..... { <identifier>: <type> => .... }
 * 子类型中使用的标准标识符是"self"，不过也可以使用任何其他的标识符。
 */
class A1 {def hi = "hi"}
//trait B1有一个自类型，这要求这个trait只能增加到指定类型(类A1)的一个自类型。
trait B1 { self:A1 => override def toString = "B: "+hi }
class C1 extends A1 with B1
new C1()

/**
 * trait需要调用TestSuite.start()，但不能扩展TestSuite，因为它需要硬编码的输入参数。
 * 通过使用一个自类型，就可以认为这个trait是TestSuite的一个自类型，而不需要显示声明。
 * 利用自类型，trait可以扩展一个类而不用指定其输入参数。
 */
class TestSuite(suiteName:String){ def start(){} }
trait RandomSeeded { self:TestSuite =>
  def randomStart(): Unit ={
    util.Random.setSeed(System.currentTimeMillis())
    self.start()
  }
}
class IdSpec extends TestSuite("ID Tests") with RandomSeeded {
  def testId() { println(util.Random.nextInt != 1)}
  override def start() { testId() }
  println("Starting...")
  randomStart()
}


/**
 * 4、导入类和对象成员的语法与导入包装类是一样的。
 */
case class Receipt(id:Int,amount:Double,who:String,title:String)
val latteReceipt = Receipt(123,4.12,"fred","Medium Latte")
import latteReceipt._
println(s"Sold a $title for $amount to $who")

/**
 * stream和shuffle是util.Random对象的成员，这里直接访问
 */
import util.Random._
val letters = alphanumeric.take(20).toList.mkString
val numbers = shuffle(1 to 20)





