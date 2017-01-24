package example

import scala.collection.mutable.ListBuffer

object Hello extends Greeting with App {
  println(greeting)
  /* sample of creating a raw deck
   * var d = new ListBuffer[Card]()

   * d += (
   *   new Card("p", 0),
   *   new Card("u", 2),
   *   new Card("s", 1)
   * )
   */
  val a = new Deck(Prefab.gauntlet_thirty_deck())
  a.shuffle()
  a.d foreach { c =>
    println(c.generic_type + ", " + c.cost)
  }
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
