package example

import org.scalatest._
import scala.collection.mutable.ListBuffer


class SimSpec extends FlatSpec with Matchers {
  "The Sim object" should "intialize a game of cards" in {
    var cards = new ListBuffer[Card]()
    cards += (
      new Card("p", 0),
      new Card("p", 0),
      new Card("u", 2),
      new Card("u", 3),
      new Card("s", 1),
      new Card("p", 0),
      new Card("p", 0),
      new Card("u", 2),
      new Card("u", 3),
      new Card("s", 1)
    )

    val deck = new Deck(cards)
    val s = new Sim(deck)

    s.hand.size should be (7)
    // deck.d.size should be (5)

    // val card = deck.draw
    // deck.d.size should be (4)

    // deck.replace(card)
    // deck.d.size should be (5)
  }
}
