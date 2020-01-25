/**
 * 1、使用隐含参数(implicit parameter)，调用者在其自己的命名空间提供默认值。
 * 函数可以定义一个隐含参数，通常作为与其他非隐含参数相区别的一个单独的参数组。
 * 调用者可以指示一个局部值为隐含值，作为隐含参数填入。
 * 调用函数时，如果没有为隐含参数指定值，就会使用局部隐含值，将它增加到函数调用中。
 *
 * 使用implicit关键字标志一个值、变量、函数参数为隐含的。
 * 可以用隐含值或变量（如果在当前命名空间中可用）填充一个函数调用中的隐含参数。
 */
object Doubly{
  def print(num: Double)(implicit fmt:String) = {
    println(fmt format num)
  }
}
//implicit val fmts = "%.2f"
//Doubly.print(3.724)
Doubly.print(3.724)("%.1f")

case class USD(amount:Double){
  implicit val printfmt = "%.2f"
  def print = Doubly.print(amount)
}
new USD(81.924).print


/**
 * 2、隐含类(implicit class)是一种类类型，可以与另一个类自动转换。
 * 通过提供从类型A到类型B的自动转换，类型A的实例就好像是类型B的实例一样，可以有同样的字段和方法。
 * scala编译器发现要在一个实例上访问未知的字段或方法时，就会使用隐含转换。
 * 它会检查当前命名空间的隐含转换：
 *   1、取这个实例作为一个参数；
 *   2、实现缺少的字段或方法。
 * 如果它发现一个匹配，就会向隐含类增加一个自动转换，从而支持在这个隐含类型上访问这个字段或方法。
 * 若没有匹配到，就会得到一个编译错误。
 */
object IntUtils {
  implicit class Fishies(val x:Int){
    def fishes = "Fish" * x
  }
}
import IntUtils._
println(3.fishes)

/**
 * 定义和使用隐含类还有一些限制：
 *   1、隐含类必须在另一个对象、类或trait中定义。幸运的是，对象中定义的隐含类可以很容易地导入当前命名空间。
 *   2、它们必须取一个非隐含类参数。上面例子中Int参数可以将一个Int转换为一个Fishies类，从而能访问fishes方法。
 *   3、隐含类的类名不能与当前命名空间中的另外一个对象、类或trait冲突。
 *     因此不能使用case类作为隐含类，因为它们有自动生成的伴生对象。
 *
 * 除了scala.Predef对象中的成员之外，通常不会自动获得其他隐含转换。
 * scala.Predef提供了很多类型特征，其中也包括隐含转换，以支持scala的一些表述性语法。
 * 例如箭头操作符(->)
 */
implicit class ArrowAssoc[A](x:A){
  def ->[B](y:B) = Tuple2(x,y)
}


