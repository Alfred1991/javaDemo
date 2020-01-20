import java.io.File

/**
 * scala中所有可迭代的集合的根是Iterable
 *
 * 1、List类型是一个不可变的单链表，可以作为一个函数调用List来创建一个列表，并以逗号分隔参数的形式传入列表的内容。
 */
val numbers = List(32,95,24,21,17)
val colors = List("red","green","blue")
println(s"I have ${colors.size} colors $colors")

colors.head
colors.tail
colors(1)

var total = 0;for(i <- numbers) { total += i }
for(c <- colors) { println(c) }

colors.foreach( (c:String) => println(c) )
val sizes = colors.map( _.size )
val total1 = numbers.reduce( _+_ )
val f = numbers filter (_ > 18)
val p = numbers partition ( (x:Int) => x < 31)
val s = numbers sortBy ( _.toInt )

/**
 * List是一个不可变的递归数据结构，所以列表中的每一项都有自己的表头和越来越短的表尾。
 */
var i = numbers
while(! i.isEmpty) { print(i.head + ","); i = i.tail}

def visit(i:List[Int]){ if(i.size > 0) { print(i.head + ","); visit(i.tail) }}
visit(numbers)

/**
 * 所有列表都有一个Nil实例作为终结点，所以迭代器可以通过比较当前元素和Nil来检查是否达到列表末尾.
 * Nil实际上是List[Nothing]的一个单例实例。
 * 创建一个新的空列表时，实际上会返回Nil而不是一个新实例。因为Nil是不可变的，所以它和一个全新的空列表实际上并没有区别。
 * 类似地，如果创建一个只包含一项的新列表，实际上就是在创建一个列表元素，而这个元素指向Nil作为其表尾。
 */
def visit1(i:List[Int]){ if(i != Nil) { print(i.head + ","); visit(i.tail) }}
visit1(numbers)

val l:List[Int] = List()
l == Nil

val m:List[String] = List("a")
m.head
m.tail == Nil

/**
 * 利用与Nil的这种关系，可以采用另外一种方法构造列表。
 * Scala支持使用cons(contruct的简写)操作符来构建列表。
 * 使用Nil作为基础，并使用右结合的cons操作符 :: 绑定元素，就可以创建一个列表。
 * :: 只是List的一个方法，它取一个值，并让其成为新的表头。可以将 :: 看做是一个右结合的操作符。
 *
 * 类似 ::、drop、take 这样在列表前面完成操作的，不存在性能损失（list是一个链表）。
 * 它们的反向操作是： +:（一个左结合操作符）、dropRight和takeRight。
 */
val numbers1 = 1 :: 2 :: 3 :: Nil

val first = Nil.::(1)
first.tail == Nil
val second = 2 :: first
second.tail == first

/**
  * 映射方法是指 取一个函数，将它应用于列表中的每个成员，再把结果收集到一个新列表。
  */
List(0,1,0) collect {case 1 => "ok"}
List("milk,tea") flatMap (_.split(','))
List("milk","tea") map (_.toUpperCase)

/**
 * 归约列表将列表收缩为单个值。
 */
val num1 = numbers.max
val num2 = numbers.min
val num3 = numbers.product
val num4 = numbers.sum
val validations = List(true,true,false,true,true,true)
val valid1 = !(validations contains false)
val valid2 = validations forall (_ == true)
val valid3 = validations exists {_==false}

/**
 * 归约操作只需要迭代处理一个累加器（accumulator）变量，这个变量包含目前为止的当前结果。
 */
def contains(x:Int, l:List[Int]):Boolean = {
  var a:Boolean = false
  for (i <- l){ if(!a) a = (i == x) }
  a
}

def boolReduce(l:List[Int],start:Boolean)(f:(Boolean,Int) => Boolean)={
  var a = start
  for(i <- l) a = f(a,i)
  a
}
val included = boolReduce(List(46,19,92),false){ (a,i) => if(a) a else (i == 19)}

def reduceOp[A,B](l:List[A],start:B)(f:(B,A) => B):B = {
  var a = start
  for (i <- l) a = f(a,i)
  a
}
val include = reduceOp(List(46,19,92),false){ (a,i) => if (a) a else (i == 19)}
val answer = reduceOp(List(11.3,23.5,7.2),0.0){_+_}
//flod reduce 和 scan
val include1 = List(46,19,92).foldLeft(false){ (a,i) => if (a) a else (i == 19) }
val answer1 = List(46,19,92).reduceLeft(_+_)


/**
 * 2、set是一个不可变的无序集合，其工作与List类似。
 */
val unique = Set(10,20,30,20,20,10)
val sum = unique.reduce( (a:Int,b:Int) => a + b )


/**
 * 3、Map是一个不可变的键/值库，在其他语言中也称为散列映射(hashmap)、字典(dictionary)或关联数组(associative array)。
 * 创建Map时，指定键值对为元组，可以使用关系操作符(->)来指定键和值元组。
 */
val colorMap = Map(("red",0xFF0000),"green" -> 0xFF00,"blue" -> 0xFF)
val redRGB = colorMap("red")
val cyanRGB = colorMap("green") | colorMap("blue")
val hasWhite = colorMap.contains("white")
for (pairs <- colorMap) { println(pairs) }


/**
 * 4、转换集合
 */
List(24,99,104).mkString(",")
List('f','t').toBuffer //将一个不可变集合转换为一个可变的集合
Map("a" -> 1,"b" -> 2).toList
Set( 1 -> true,3 -> true).toMap
List(2,5,5,3,2).toSet
List(2,5,5,3,2).toString


/**
 * 5、默认情况下，scala的集合和java的集合之间是不兼容的。
 */
import scala.jdk.CollectionConverters._
List(12,29).asJava
new java.util.ArrayList(5).asScala


/**
 * 6、使用集合的模式匹配
 */
val statuses = List(500,404)
//利用模式哨卫
val msg = statuses.head match {
  case x if x<500 => "okay"
  case _ => "whoah, an error"
}
//由于集合支持等号(==)，它们支持模式匹配
val msg1 = statuses match {
  case List(404,500) => "not found & error"
  case List(500,404) => "error & not found"
  case List(404,500) => "okay"
  case _ => "not sure what happened"
}
//值绑定
val msg2 = statuses match {
  case List(500,x) => s"Error followed by $x"
  case List(e,x) => s"$e was followed by $x"
}
//列表可分解为表头元素和表尾，因此可以匹配表头和表尾
val head = List('r','g','b') match {
  case x :: xs => x
  case Nil => ' '
}
//元组不是正式的集合，但也支持模式匹配和值绑定
val code=('h',204,true) match {
  case (_,_,false) => 501
  case ('c',_,true) => 302
  case ('h',x,true) => x
  case (c,x,true) => {
    println(s"Did ")
  }
}


/**
 * 不可变集合List、Set和Map在创建之后不能改变。不过，确实可以把它们变换到新集合。
 */
val m1 = Map( "AAPL" -> 597, "MSFT" -> 40 )
val n1 = m1 - "AAPL" + ("GOOG" -> 521)

/**
 * 7、要修改集合，最直接的方法是利用一个可变的集合类型。
 *   collection.immutable.List collection.mutable.Buffer
 *   collection.immutable.Set collection.mutable.Set
 *   collection.immutable.Map collection.mutable.Map
 *
 * collection.mutable.Buffer类型是一个通用的可变序列，支持在开头、中间或末尾增加元素。
 */
val nums = collection.mutable.Buffer(1)
for (i <- 2 to 10) nums += i
println(nums)

val nums1 = collection.mutable.Buffer[Int]()
for (i <- 1 to 10) nums += i
println(nums1)

/**
 * Builder是Buffer的一个简化形式，仅限于生成指定的集合类型，而且只支持追加操作。
 * 要为一个特定集合类型创建构建器，可调用该类型的newBuilder方法。
 * 调用构建器的result方法可将其转换为最终的Set。
 *
 * 若只是迭代地构建一个可变集合，并将它转化为不可变的集合，Builder类型就是一个很好的选择。
 * 如果在构建可变集合时需要Iterable操作，或者不打算把它转换为不可变的集合，那么更好的选择是使用Buffer或其他可变集合类型。
 */
val b = Set.newBuilder[Char]
b += 'h'
b ++= List('e','l','l','o')
val helloSet = b.result


/**
 * 8、Array是一个大小固定的可变索引集合。
 * 它不是正式意义上的集合，因为它不在"scala.collections"包里，且没有扩展根类型Iterable。
 * Array类型实际上只是Java数组类型的一个包装器，另外还提供一个称为隐含类(implicit class)的高级特性，使它可以像序列一样使用。
 */
val colors1 = Array("red","green","blue")
colors1(0) = "purple"
colors1
println("very purple: "+colors1)
val files = new File(".").listFiles()
val scala = files map (_.getName) filter (_ endsWith "sc")


/**
 * 9、Seq是所有序列的根类型，包括类似List的链表和类似Vector的索引（直接存取）列表。
 *   Seq
 *     IndexedSeq
 *       Vector  以一个Array提供后备存储，可根据索引直接访问元素
 *       Range
 *     LinearSeq
 *       Queue/Stack
 *       List
 *         Nil
 *       Stream
 * 如果Array类型是一个集合，可以认为它是一个索引序列，因为可以直接访问元素不需要遍历。
 * Seq本身不能实例化，但可以调用Seq创建List。
 */
val inks = Seq('C','M','Y','K')

val hi = "Hello, "++"worldly" take 12 replaceAll ("w","W")


/**
 * 10、Stream类型是一个懒(lazy)集合，由一个或多个起始元素和一个递归函数生成。
 * 第一次访问元素时才会把这个元素增加到集合中，这与其他不可变集合正好相反，不可变集合要在实例化时接收全部内容。
 * 流生成的元素会缓存，确保每个元素只生成一次。
 * 流可能是无界的，理论上是无限的集合，只是在访问元素时才会生成这个元素。
 * 流也可以以Stream.Empty结束，这对应于List.Nil。
 * 流也是递归数据结构，包括一个表头（当前元素）和一个表尾（集合的其余部分）。
 * 可以利用一个函数以及该函数的递归调用来构建，这个函数返回一个新的流（其中包含表头元素），该函数的递归调用可以构建表尾。
 * 可使用Stream.cons用表头和表尾构建一个新的流
 */
def inc(i:Int):Stream[Int] = Stream.cons(i,inc(i+1))
val s1 = inc(1)
val l1 = s1.take(5).toList
s1

/**
 * 使用 #:: 操作符创建Stream
 */
def inc1(head:Int):Stream[Int] = head #:: inc(head+1)
inc1(10).take(10).toList

/**
 * 创建有界的流
 */
def to(head:Char,end:Char):Stream[Char] = (head > end) match {
  case true => Stream.empty
  case false => head #:: to((head+1).toChar,end)
}
val hexChars = to('A','Z').take(20).toList


/**
 * 11、一元集合(monadic)支持类似Iterable中的变换操作，但是包含的元素不能多于1个。
 *
 * Option类型表示一个值的存在或不存在。
 * 这个值可能没有，因此可以包装在一个Option集合中，从而清楚地指示它有可能不存在。
 * Option类型本身没有实现，而是依赖两个子类型提供具体实现：Some和None。
 * Some是一个类型参数化的单元素集合，None是一个空集合。None类型没有类型参数，因为它永远不包含任何内容。
 */
var x:String = "Indeed"
var a1 = Option(x)
x = null
var b1 = Option(x)
//使用isDefined和isEmpty分别检查一个给定的Option是Some还是None
println(s"a is defined? ${a1.isDefined}")
println(s"b is not defined? ${b1.isEmpty}")

def divide(amt:Double,divisor:Double):Option[Double] = {
  if(divisor == 0) None
  else Option(amt/divisor)
}
val legit = divide(5,2)
val illegit = divide(3,0)


/**
 * Scala的集合使用Option类型提供安全的操作来处理空集合的情况。
 * 例如head操作对于空列表会抛出错误，更安全的方式是headOption
 */
val odds = List(1,3,5)
val firstOdd = odds.headOption
val events = odds filter (_ % 2 == 0)
val firstEven = events.headOption

/**
 * find操作提供了集合选项的另一种用法，它结合了filter和headOption，将返回与一个谓词函数匹配的第一个元素。
 */
val words = List("risible","scavenger","gist")
val uppercase = words find (w => w == w.toUpperCase())
val lowercase = words find (w => w == w.toLowerCase())

val filtered = lowercase filter ( _ endsWith "ible" ) map (_.toUpperCase())
val exactSize = filtered filter (_.size > 15) map (_.size)


