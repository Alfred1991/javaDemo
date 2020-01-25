/**
 * 类(class)是一个可以包含数据和方法的实体，有一个特定的定义。
 * 类型(type)是一个类规范，与符合其需求的一个类或一组类匹配。
 * 例如Option类是一个类型，Option[Int]也是一个类型。
 * 类型可以使一个关系，指定"类A或其他任何子孙类"，或者"类B或其任何父类"。
 * 类型还可以更为抽象，指定"定义这个方法的任何类"
 *
 * 类型是类规范，但也同样适用于trait。
 * 对象并不认为是类型。对象是单例的，尽管它们可以扩展类型，但它们本身不是类型。
 *
 * 1、类型别名(type alias)会为一个特定的现有类型(或类)创建一个新的命名类型。
 * 编译器处理这个新类型别名时，就好像它在一个常规类中定义一样。
 * 但与隐含转换类似，类型别名只能在对象、类或trait中定义。它们只适用于类型，所以对象不能创建类型别名。
 *   type <identifier>[type parameters] = <type name>[type parameters]
 */
object TypeFun {
  type Whole = Int
  val x:Whole = 5

  type UserInfo = Tuple2[Int,String]
  val u:UserInfo = new UserInfo(123,"George")

  type T3[A,B,C] = Tuple3[A,B,C]
  val things = new T3(1,'a',true)
}
val x = TypeFun.x
val u = TypeFun.u
val things = TypeFun.things


/**
 * 2、抽象类型(abstract types)是规范，可以解析为0、1个或多个类。
 * 它们的做法与类型别名类似，但作为规范，它们是抽象的，不能用来创建实例。
 * 抽象类型常用于类型参数，来指定一组可以接受(可以传入)的类型。
 * 抽象类型还可用来创建抽象类中的类型声明(type declarations)，即声明具体(非抽象)子类必须实现的类型。
 */
class User(val name:String)
trait Factory { type A; def create:A }
trait UserFactory extends Factory {
  type A = User
  def create = new User("")
}

/**
 * 还可以采用另一种方法写trait和类：使用类型参数。
 */
trait Factory1[A] { def create:A }
trait UserFactory1 extends Factory1[User] { def create = new User("") }


/**
 * 3、定界类型限制为只能是一个特定的类或他的子类型或基类型。
 * 上界(upper bound)限制一个类型只能是该类型或它的某个子类型。
 * 下界(lower bound)限制一个类型只能是该类型或它扩展的某个基类型。
 *   <identifier> <: <upper bound type>
 *   <identifier> >: <lower bound type>
 */
class BaseUser(val name:String)
class Admin(name:String,val level:String) extends BaseUser(name)
class Customer(name:String) extends BaseUser(name)
class PreferredCustomer(name:String) extends Customer(name)

def check[ A <: BaseUser ](u:A) { if (u.name.isEmpty) println("Fail!") }
check(new Customer("Fred"))
check(new Admin("","strict"))

def recruit[A >: Customer](u:Customer):A = u match {
  case p:PreferredCustomer => new PreferredCustomer(u.name)
  case c:Customer => new Customer(u.name)
}
val customer = recruit(new Customer("Fred"))
val preferred = recruit(new PreferredCustomer("George"))

/**
 * 定界类型还可用来声明抽象类型。
 */
abstract class Card {
  type UserType <: BaseUser
  def verify(u:UserType):Boolean
}
class SecurityCard extends Card{
  type UserType = Admin
  def verify(u:Admin) = true
}
val v1 = new SecurityCard().verify(new Admin("George","high"))
class GiftCard extends Card{
  type UserType = Customer
  def verify(u:Customer) = true
}
val v2 = new GiftCard().verify(new Customer("Fred"))


/**
 * 增加上界或下界可以使类型参数更为限定，增加类型变化则相反，会减少类型参数的限制。
 * 4、类型变化(Type variance)指定一个类型参数如何调整以满足一个基类型或子类型。
 * 默认地，类型参数是不变的(invariant)。对于一个类型参数化的类，它的实例只与该类以及参数化类型兼容。
 * 这个实例不能存储在类型参数为其基类的值中。
 */
class Car { override def toString = "Car()" }
class Volvo extends Car { override def toString = "Volvo()" }
val c:Car = new Volvo

case class Item[A](a:A) { def get:A = a }
//此处编译不通过
//val c:Item[Car] = new Item[Volvo](new Volvo)

/**
 * 为修正这个问题，需把类型参数放在Item covariant中。
 * 协变类型参数(Covariant type parameters)可以自动在必要时调整为其基类型。
 * 通过在类型参数前加一个加号(+)，可以标志一个类型参数为协变类型参数。
 * 此时Item1[Volvo]协变为Item[Car]，因此可以完成下面的赋值。
 */
case class Item1[+A](a:A) { def get:A = a }
val c1:Item1[Car] = new Item1[Volvo](new Volvo)
val auto = c1.get

/**
 * 方法输入参数不能协变，这与基类型不能转换为子类型的原因相同。
 * 若输入参数是协变的，那么它会绑定到一个子类型，但是可以用一个基类型调用，这是不可能的。
 * 此时，方法参数中使用的类型参数是逆变的(contravariant)，而不是协变。
 * 逆变是指一个类型参数可以调整为一个子类型，与子类型到基类型的多态转换相反。
 * 逆变类型参数要在类型参数前标志一个减号(-)。
 * 它们可以用于方法的输入参数，但不能用作方法的返回类型。返回类型是协变的，因为其结果可以是一个子类型。
 */
class Check[-A] { def check(a:A) = {} }

class Car2;class Volvo2 extends Car2;class VolvoWagon2 extends Volvo2
class Item2[+A](a:A) { def get: A = a }
class Check2[-A](a:A) { def check(a:A) = {} }
def item2(v:Item2[Volvo2]){ val c:Car2 = v.get }
def check2(v:Check2[Volvo2]){ v.check(new VolvoWagon2()) }

//协变类型参数可以变换为其父类，但无法变换为其子类。本例Car2无法变换为Volvo2。
//item2(new Item2[Car2](new Car2()))
item2(new Item2[Volvo2](new Volvo2()))
item2(new Item2[VolvoWagon2](new VolvoWagon2))

check2(new Check2[Car2](new Car2()))
check2(new Check2[Volvo2](new Volvo2()))
//逆变类型参数可以变换为其子类，但无法变换为其父类。本例VolvoWagon2无法变换为Volvo2。
//check2(new Check2[VolvoWagon2](new VolvoWagon2()))


/**
 * 隐含参数、隐含转换、类型别名只能在其他类型中定义，这个限制用来保护这些实体，确保多数情况下只能通过显式导入增加到命名空间。
 * scala.Predef对象是一个例外，另一个例外是包对象。
 *
 * 5、包对象是对应各个包的唯一对象，会自动导入到该包代码的命名空间中。
 * 包对象在自己单独的文件中定义，即package.scala，这个文件会放在将受其影响的包中。
 * 可以在对象定义前面增加package关键字来定义包对象。
 */
//located on com/oreilly/package.scala
package object oreilly {
  type Mappy[A,B] = collection.mutable.HashMap[A,B]
}


