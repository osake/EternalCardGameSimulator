package example

import org.scalatest._
import scala.collection.mutable.ListBuffer


class DeckSpec extends FlatSpec with Matchers {
  "The Card object" should "intialize a deck of cards" in {
    var cards = new ListBuffer[Card]()
    cards += (
      new Card("p", 0),
      new Card("p", 0),
      new Card("u", 2),
      new Card("u", 3),
      new Card("s", 1)
    )
    val deck = new Deck(cards)
    deck.d.size should be (5)
  }
}
