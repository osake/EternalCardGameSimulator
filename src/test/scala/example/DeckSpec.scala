package example

import org.scalatest._
import scala.collection.mutable.ListBuffer
import example.model.Card
import example.model.Deck


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
    deck.cards.size should be (5)

    val card = deck.draw.get
    deck.cards.size should be (4)

    deck.replace(card)
    deck.cards.size should be (5)
  }
}
