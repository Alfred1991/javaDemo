/**
 * 1、val表示值，var表示变量
 */
val value:String = "我是值"
var variable:String = "我是变量"


/**
 * 2、字符串内插：
 *   s"xxx${ref}",其中ref是一个外部引用或表达式，当无歧义时可以去掉大括号
 *   字符串内插的替代格式是使用printf记法，此时需把前缀改为"f"，然后将printf的记法紧跟在引用后面
 */
val item="apple"
s"How do you like them ${item}s"
f"I wrote a new $item%.3s"
f"Enjoying this $item ${355/113.0}%.5f times today"


/**
 * 3、正则表达式：
 *   使用.r操作符将字符串转换为正则表达式类型，这会返回一个Regex实例。
 */
val input = "Enjoying this apple 3.14159 times today"
val pattern = raw".* apple ([\d.]+) times .*".r
val π = input match {
  case pattern(π) => s"$π".toDouble
}


/**
 * 4、Scala核心（数值和非数值）类型的层次体系
 * Any
 *   AnyVal
 *     Numeric Types
 *     Char
 *     Boolean
 *   AnyRef
 *     Collections
 *     Classes
 *     String
 *
 * Null
 *   Nothing
 *
 * 其中Null和Nothing是所有类型的子类型。
 * Unit类型与java中void类似，作为不返回任何结果的函数或表达式的返回类型
 * scala中常用类型操作：
 *   asInstanceOf[<type>]
 *   getClass
 *   isInstanceOf
 *   hashCode
 *   to<type>
 *   toString
 */
val c = '我'
val s:Int = c
val isTrue = !true
val unequal = (5 != 6)
val nada = ()


/**
 * 5、元组是一个包含两个或多个值的有序容器，所有这些值可以有不同的类型。
 */
val info = (5,"Korben",true)
info._2
val red = "red" -> "0xff0000"
val reversed = red._2 -> red._1


/**
 * 6、可以使用大括号结合多个表达式(expression)创建一个表达式块。
 * 块中最后一个表达式确定了块的返回值。
 * 没有返回值的表达式就是语句(statement)。
 */
val amount = {
  val x = 5 *20
  x+10
}


/**
 * 7、if-else
 *  if(<Boolean expression>) <expression>
 *  else <expression>
 */
if ( 47 % 3 > 0 ) println("Not a multiple of 3.")
val result = if (false) "what does this return?"
val x = 10; val y = 20
val max = if(x > y) x else y


/**
 * 8、匹配表达式（match expression）类似C和Java的"switch"语句。
 *   <expression> match {
 *     case <pattern match> => <expression>
 *      [case ...]
 *   }
 */
val xx = 10; val yy = 20
val max = xx > yy match {
  case true => xx
  case false => yy
}
val status = 500
val message = status match {
  case 200 =>
    "ok"
  case 400 => {
    println("ERROR - we called the service incorrectly")
    "error"
  }
  case 500 => {
    println("ERROR - the service encountered an error")
    "error"
  }
}
val day = "MON"
val kind = day match {
  case "MON"|"TUE"|"WED"|"THU"|"FRI" =>
    "weekday"
  case "SAT"|"SUN" =>
    "weekend"
}
//通配模式匹配：值绑定和通配符（即下划线）
//值绑定的变量名可以任意指定，此处是others
val message1 = "ok"
val status1 = message1 match {
  case "ok" => 200
  case others => {
    println(s"Couldn`t parse $others")
    -1
  }
}
//通配符
val message2 = "Unauthorized"
val status2 = message2 match {
  case "ok" => 200
  case _ => {
    println(s"Couldn`t parse $message2")
    -1
  }
}
//模式哨卫(pattern guard)向值绑定模式中增加一个if表达式，从而可以为匹配表达式增加条件逻辑
// case <pattern> if <Boolean expression> => <one or more expression>
val response : String = null
response match {
  case s if s != null => println(s"Received '$s'")
  case s => println("Error! Received a null response")
}
//用模式变量(pattern variables)匹配类型
val x1:Int = 12180
val y1:Any = x1
y1 match {
  case x:String => s"$x1"
  case x:Double => f"$x1%.2f"
  case x:Float => f"$x1%.2f"
  case x:Long => s"${x1}l"
  case x:Int => s"${x1}i"
}


/**
 * 9、循环
 *   数据结构Range：
 *     <starting integer> [to|until] <ending integer> [by increment]
 *
 *   for循环：
 *     for (<identifier> <- <iterator>) [yield] [<expression>]
 *     当指定yield时，调用的所有表达式的返回值将作为一个集合返回。
 */
val res00 = for(x <- 1 to 7 by 1) yield { println(s"Day $x:") ; s"Day $x:"}
for (day <- res00) print(day + ",")
//迭代器哨卫(iterator guard)也称为过滤器(filter)，可以为迭代器增加一个if表达式
//  for(<identifier> <- <iterator> if <Boolean expression>) ...
val quote = "Faith,Hope,,Charity"
for (
  t <- quote.split(",")
  if t != null
  if t.size > 0
){println(t)}
//嵌套迭代器(nested iterators)是增加一个for循环的额外的迭代器
for ( x <- 1 to 2; y <- 1 to 3){ print(s"($x,$y)")}
//for循环中的值绑定(value binding)
//for(<identifier> <- <iterator>;<identifier> = <expression>) ...
val powersOf2 = for(i <- 0 to 8;pow = 1 << i) yield pow


/**
 * while 和 do-while循环：
 *   while (<Boolean expression>) statement
 */
var x2 = 10;while (x2 > 0) x2 -= 1

val x3 = 0
do println(s"Here I am, x = $x3") while (x3 > 0)

