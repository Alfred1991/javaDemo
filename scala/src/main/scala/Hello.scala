package main.scala

object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello from SBT")
  }
  def upper(strings: String*) = strings.map(_.toUpperCase())

}
