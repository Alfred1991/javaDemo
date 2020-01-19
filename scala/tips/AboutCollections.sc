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





