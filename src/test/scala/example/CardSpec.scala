package example

import org.scalatest._

class CardSpec extends FlatSpec with Matchers {
  "The Card object" should "have a cost and a type" in {
    val c = new Card(generic_type = "p", cost = 0)
    c.generic_type should be ("p")
    c.cost should be (0)
  }
}
