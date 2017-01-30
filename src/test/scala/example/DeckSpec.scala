package example

import org.scalatest._
import scala.collection.mutable.ListBuffer


class DeckSpec extends FlatSpec with Matchers {
  "The Deck object" should "intialize a deck of cards" in {
    var cards = new ListBuffer[Card]()
    cards += (
      new Card(),
      new Card(),
      new Card(),
      new Card(),
      new Card()
    )
    val deck = new Deck(cards)
    deck.deck.size should be (5)

    val card = deck.draw
    deck.deck.size should be (4)

    deck.replace(card)
    deck.deck.size should be (5)
  }
}
