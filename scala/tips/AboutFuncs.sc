import com.sun.javafx.iio.common.ImageLoaderImpl

/**
 * 1、scala中，函数(functions)是可重用的命名表达式。函数可以参数化，可以返回一个值。
 *
 * 如果遵循函数式编程方法论，尽可能构建纯(pure)函数，还会得到更大的好处。纯函数是指：
 *   1、有一个或多个输入参数。
 *   2、只使用输入参数完成计算。
 *   3、返回一个值。
 *   4、对于相同的输入总返回相同的值。
 *   5、不使用或影响函数之外的任何数据。
 *   6、不受函数之外的任何数据的影响。
 * 通常要保证以适当的方式清晰地命名和组织非纯函数，以便与纯函数区分，这是模块化和组织Scala应用的一个常见目标。
 */

/**
 * 定义无输入函数
 *   def <identifier> = <expression>
 */
def hi = "hi"

/**
 * 定义函数时指定返回类型
 *   def <identifier>: <type> = <expression>
 */
def hi1:String = "hi"

/**
 * 定义函数
 *   def <identifier>(<identifier>: <type>[, ... ]): <type> = <expression>
 */
def multiplier(x: Int, y: Int): Int = { x * y }

/**
 * 过程(procedure)是没有返回值的函数。以一个语句结尾的函数也是一个过程。
 * 如果有一个简单的函数，没有显示的返回值，而且最后是一个语句，scala就会推导该函数的返回类型为Unit。
 */
def log(d:Double) = println(f"Got value $d%.2f")
def log1(d:Double):Unit = println(f"Got value $d%.2f")

/**
 * 用空括号定义函数
 *   def <identifier>()[: <type>] = <expression>
 *
 * 与"副作用"的函数应当使用小括号
 */
def hi2(): String = "hi"
def hi3() = "hi"

/**
 * 使用表达式块调用函数
 *   <function identifier> <expression block>
 * 表达式块会在调用函数之前计算，而且表达式块的返回值将用作这个函数的参数。
 */
def formatEuro(amt:Double) = f"€$amt%.2f"
formatEuro(3.4645)
formatEuro{ val rate = 1.32; 0.235 + 0.7123 +rate * 5.32}

/**
 * 递归(recursive)函数指调用自身的函数，可能要检查某类参数或外部条件来避免函数调用陷入无限循环。
 *
 * 为避免"栈溢出"错误，scala编译器可以用尾递归(tail-recursion)优化一些递归函数，使得递归调用不使用额外的栈空间。
 * 对于利用尾递归优化的函数，递归调用不会创建新的栈空间，而是使用当前函数的栈空间。
 * 只有最后一个语句是递归调用的函数才能由Scala编译器完成尾递归优化。
 * 可以利用函数注解(function annotation)来标志一个函数将完成尾递归优化。
 */
@annotation.tailrec
def power(x:Int,n:Int,t:Int=1): Long = {
  if (n < 1) t
  else power(x,n-1,x*t)
}
power(2,8)

/**
 * 嵌套函数
 *   有时需要在一个方法中重复某个逻辑，但把它作为一个外部方法又没有太大意义。
 *   此时可以在函数中定义另一个内部函数，这个内部函数只能在该函数中使用。
 *
 * 这里的嵌套函数与外部函数同名，不过，由于它们的参数不同，所以不会产生冲突。
 * Scala函数按函数名以及其参数类型列表来区分。
 * 不过即便函数名和参数类型都相同，它们也不会冲突，因为局部（嵌套）函数优先于外部函数。
 */
def max(a:Int,b:Int,c:Int) = {
  def max(x:Int,y:Int) = if (x>y) x else y
  max(a,max(b,c))
}

/**
 * 用命名参数调用函数
 *   按名调用参数，这样就允许不按顺序指定参数
 *   <function name>(<parameter> = <value>)
 */
def greet(prefix: String,name: String) = s"$prefix $name"
val greeting1 = greet("Ms","Brown")
val greeting2 = greet(name = "Brown",prefix = "Mr")

/**
 * 有默认值参数
 *   def <identifier>(<identifer>: <type> = <value>): <type>
 */
def greet1(prefix: String = "",name: String) = s"$prefix $name"
val greeting3 = greet1(name = "Paul")
def greet2(name: String,prefix: String = "") = s"$prefix $name"
val greeting4 = greet2("Paul")

/**
 * Vararg参数
 *   这是一个函数参数，可以匹配调用者的0个或多个实参。
 *   要标志一个参数匹配一个或多个输入实参，在函数定义中需要该参数类型后面增加一个星号(*)
 */
def sum(items: Int*):Int = {
  var total = 0
  for (i <- items) total += i
  total
}
sum(10,20,30)
sum()

/**
 * 参数组：
 *   scala可以把参数表分解为参数组(parameter groups)，每个参数组分别用小括号分隔
 */
def max1(x:Int)(y:Int) = if (x>y) x else y
val larger = max1(20)(39)

/**
 * 类型参数
 *   def <function-name>[type-name](<parameter-name>:<type-name>): <type-name>...
 */
def identity[A](a:A):A = a
val s = identity("Hello")
val d:Double = identity[Double](2.717)


/**
 * 在实际中，函数常存在于对象中，用来处理对象的数据，所以对函数更适合的说法通常是"方法"。
 * 方法（method）是类中定义的一个函数，这个类的所有实例都会有这个方法。
 *   <class instance>.<method>[(<parameters>)]
 *
 * 最后一个方法名只有一个字符，即一个加号(+)，不过这仍是一个合法的函数。
 * 我们完全可以直接使用加法操作符，不过这个方法实际上正式加法操作符的具体实现。
 * scala中实际上没有加法操作符，也没有任何其他算术运算符。
 * 我们在Scala中使用的所有算术运算符其实都是方法，写为简单的函数，它们使用相应的操作符符号作为函数名，并绑定到一个特定的类型。
 * 之所以这么做，是因为还可以采用另一种形式调用对象的方法，这称为操作符记法（operator notation），这里不使用传统的点记法，而是使用空格来分隔对象、操作符方法和方法的参数（只有一个参数）
 * 每次写2 + 3时，scala编译器会把它识别为操作符记法，并相应地处理，就好像写2.+(3)一样，这里调用了值为2的一个Int的加法方法，并提供参数3，最后会返回值5。
 * 要采用操作符记法调用对象的方法，要求这个方法只有一个参数(多参数时可将其打包为一个tuple)，另外对象、方法和这个参数之间要用空格分隔。
 *
 * 操作符记法调用方法：
 *   <object> <method> <parameter>
 * 对于这种记法，更准确的说法应当是中缀操作符记法（infix operator notation），因为操作符位于两个操作数中间。
 */
"vacation.jpg".endsWith(".jpg")
65.642.round
65.642.compareTo(18)
65.642.+(2.721)
//多参数时将其打包为一个tuple
"starting" substring (1,4)


/**
 * 函数式编程的一个关键是函数应当是首类的（first-class）。
 * "首类"表示函数不仅能得到声明和调用，还可以作为一个数据类型用在这个语言的任何地方。
 * 首类函数与其他数据类型一样，可以采用字面量形式创建，而不必指定标识符；或者存储在一个容器中，如值、变量或数据结构；还可以用作外另一个函数的参数或返回值。
 * 如果一个函数接受其他函数作为参数，或者使用函数作为返回值，这就称为高阶函数(higher-order functions)，如map()和reduce()。
 * 使用高阶函数处理数据的一个好处是：具体如何处理数据将作为实现细节，留给包含这个高阶函数的框架来完成。
 * 这种方法实际上有一个名字：声明式编程（declarative programming），与之相反的是比较强制性的命令式编程（imperative programming）。
 *
 * 函数的类型(type)是其输入类型和返回值类型的一个简单组合
 *   ([<type>, ...]) => <type>
 * 此前使用的所有类型都只有一个简单的词，如String和Int，这让函数的类型看上去非常的特别。
 * myDouble值必须有显示的类型，以区分出它时一个函数值，而不是一个函数调用。
*/
def double(x:Int): Int = x * 2
double(5)
val myDouble:(Int)=>Int = double
myDouble(5)
val myDoubleCopy = myDouble
myDoubleCopy(5)
def max2(a:Int,b:Int) = if (a>b) a else b
val maximize:(Int,Int) => Int = max2
maximize(50,30)
def logStart() = {"=" * 50 + "\nStarting NOW\n" + "*" * 50}
val start: () => String = logStart
println(start)

/**
 * 定义函数值以及函数赋值的另外一种做法是使用通配符 _
 *   val <identifier> = <function name> _
 * 此时不需要myDouble的显式函数类型来区分函数调用。下划线(_)相当于一个占位符，表示将来的一个函数调用，这会返回一个函数值。
 */
val myDouble1 = double _
val amount = myDouble1(20)

/**
 * 高阶函数
 */
def safeStringOp(s:String,f:String=>String)={
  if (s!= null) f(s) else s
}
def reverser(s:String) = s.reverse
safeStringOp(null,reverser)
safeStringOp("Ready",reverser)

/**
 * 函数字面量(function literal或匿名函数anonymous function)可以存储在函数值和变量中，或者也可以定义为一个高阶函数调用的一部分。
 *   ([<identifier>: <type>, ...]) => <expression>
 * 任何接受函数类型的地方都可以使用函数字面量。
 */
val doubler = (x:Int) => x * 2
val double1 = doubler(22)
val greeter = (name:String) => s"Hello, $name"
val hi4 = greeter("World")
val start1 = () => "=" * 50 + "\nStarting NOW\n" + "*" * 50

/**
 * 在高阶函数调用中定义函数字面量
 */
safeStringOp(null,(s:String) => s.reverse)
safeStringOp("Ready",(s:String) => s.reverse)

/**
 * 上面的例子中，函数参数"f"的类型为String => String，因此可以从函数字面量中删除显式类型，编译器能很容易地推导出它的类型。
 */
safeStringOp(null,s => s.reverse)
safeStringOp("Ready",s => s.reverse)

/**
 * 占位符语法(placeholder syntax)是函数字面量的一种缩写形式，将命名参数替换为通配符(_)。
 * 可以在以下情况使用：
 *   1、函数的显式类型在字面量之外指定。
 *   2、参数最多只使用一次。
 */
val doubler1: Int => Int = _ * 2
safeStringOp(null,_.reverse)
safeStringOp("Ready",_.reverse)
def combination(x:Int,y:Int,f:(Int,Int)=>Int) = f(x,y)

/**
 * 使用了两个占位符，它们会按位置替换输入参数(分别是x和y)
 */
combination(23,12,_*_)
def tripleOp(a:Int,b:Int,c:Int,f:(Int,Int,Int)=>Int) = f(a,b,c)
tripleOp(23,92,14,_*_+_)
def tripleOp1[A,B](a:A,b:A,c:A,f:(A,A,A)=>B) = f(a,b,c)
tripleOp1[Int,Int](23,92,14,_*_+_)

/**
 * 部分应用函数
 */
def factorOf(x:Int,y:Int) = y % x == 0
val f = factorOf _
val multipleOf3 = factorOf(3,_:Int)
multipleOf3(78)

/**
 * 使用有多个参数表的函数，应用一个参数表中的参数，另一个参数表不应用。
 * 这种技术称为函数柯里化(currying)
 */
def factorOf1(x:Int)(y:Int) = y % x == 0
val isEven = factorOf1(2) _
val z = isEven(32)

/**
 * 指定传名参数
 *   <identifier>: => <type>
 * 传名参数相对于高阶函数中的函数参数，省略了函数的参数部分。因此可以传递任意参数的函数，甚至是表达式块。
 * 在函数中使用传名参数时可直接当做返回值的类型使用，而不是当做函数类型。
 */
def doubles(x: => Int) = {
  println("Now doubling" + x)
  x * 2
}
doubles(5)
def f(i:Int) = { println(s"Hello from f($i)"); i }
doubles(f(8))


/**
 * 目前为止研究的所有函数都称为全函数(total functions)，因为它们能正确地支持满足输入参数类型的所有可能的值。
 * 例如def double(x:Int) = x*2就是全函数，没有double()函数不能处理的参数x
 *
 * 有些函数并不能支持满足输入类型的所有可能的值，它们只能部分应用于输入数据。这种函数称为偏函数(partial functions)
 * 例如一个函数返回输入数的平方根，如果这个输入数是负数，它就不能工作。
 * scala的偏函数是可以对输入应用一系列case模式的函数字面量，要求输入至少与给定的模式之一匹配。
 * 调用一个偏函数时，如果所使用的数据不能满足其中至少一个case模式，就会导致一个Scala错误。
 */
val statusHandler: Int => String = {
  case 200 => "Okey"
  case 400 => "Your Error"
  case 500 => "Our error"
  case other => s"$other not support"
}
statusHandler(200)
statusHandler(800)


/**
 * 用函数字面量块调用高阶函数
 */
val uuid = java.util.UUID.randomUUID().toString
val timedUUID = safeStringOp(uuid, s =>
{
  val now = System.currentTimeMillis()
  val timed = s.take(24) + now
  timed.toUpperCase()
})

def safeStringOp1(s:String)(f:String => String) = {
  if (s != null) f(s) else s
}
val timedUUID1 = safeStringOp1(uuid) {
  s =>
    val now = System.currentTimeMillis()
    val timed = s.take(24) + now
    timed.toUpperCase()
}

def timer[A](f: => A): A = {
  def now = System.currentTimeMillis()
  val start = now;val a = f;val end = now;
  println(s"Executed in ${end - start} ms")
  a
}
val veryRandomAmount = timer{
  util.Random.setSeed(System.currentTimeMillis())
  for( i <- 1 to 100000 ) util.Random.nextDouble()
  util.Random.nextDouble()
}





