package main.scala

object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello from SBT")
    Hello.setFirstName("a").setFirstName("b")
  }
  def upper(strings: String*) = strings.map(_.toUpperCase())
  def setFirstName(str:String): this.type ={
    println(str)
    this
  }
}
