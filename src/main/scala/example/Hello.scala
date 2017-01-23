package example

object Hello extends Greeting with App {
  println(greeting)
  val a = new Deck(
    Array(
      new Card("p", 0),
      new Card("u", 2),
      new Card("s", 1)
    )
  )
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
