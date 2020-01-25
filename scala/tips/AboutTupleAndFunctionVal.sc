/**
 * 元组实现为TupleX[Y] case类的实例，"X"是一个从1到22的数，这表示其元数。
 * 类型参数"Y"可以是单个类型参数，对应Tuple1，或者两个类型参数(对应Tuple2)，直到22个类型参数(对应Tuple22)。
 * 用小括号语法创建元组时，会用这个值实例化一个参数个数相同的元组类。
 * TupleX[Y] case类分别用相同的数扩展一个ProductX trait。
 * 这些trait提供了一些操作，如productArity可以返回元组的元数，productElement提供了一种非类型安全的方式来访问元组的第n个元素。
 * 它们还提供了伴生对象，实现了unapply来支持元组的模式匹配。
 */
val t1:(Int,Char) = (1,'a')
val t2:(Int,Char) = Tuple2[Int,Char](1,'a')

val x:(Int,Int) = Tuple2(10,20)
println("Does the arity = 2? "+(x.productArity == 2))

/**
 * 函数值实现为FunctionX[Y] trait实例，根据函数的元数从0到22编号。
 * 类型参数"Y"可以是单个类型参数，对应Function0(返回值需要一个参数)，直到23个类型参数，对应Function22。
 * 不论调用一个现有的函数还是一个新的函数字面量，函数的具体逻辑都在类的apply()方法中实现。
 * 写一个函数字面量时，Scala编译器会把它转换为扩展FunctionX的一个新类中的apply()方法体。
 * 这种强制机制使得Scala的函数值与JVM兼容，这就限制所有函数都实现为类方法。
 */
val f1:Int => Int = _ + 2
val f2:Int => Int = new Function[Int,Int] {def apply(x:Int) = x * 2}

val hello1 = (n:String) => s"Hello, $n"
val h1 = hello1("Function Literals")
val hello2 = new Function1[String,String] {
  def apply(n:String) = s"Hello, $n"
}
val h2 = hello2("Function1 Instances")
println(s"hello1 = $hello1,hello2 = $hello2")

/**
 * Function1 trait包含两个特殊方法(andThen和compose)将两个或多个Function1实例结合为一个新的Function1.
 */
val doubler = (i:Int) => i * 2
val plus3 = (i:Int) => i + 3
val prepend = (doubler compose plus3)(1)
val append = (doubler andThen plus3)(1)


