package example

import org.scalatest._
import scala.collection.mutable.ListBuffer


class SimSpec extends FlatSpec with Matchers {

  def fixture =
    new {
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
    }

  "The Sim" should "intialize a game of cards" in {
    val f = fixture
    val deck = new Deck(f.cards)
    val s = new Sim(deck)

    s.hand.size should be (7)
    deck.d.size should be (3)

    val card = deck.draw
    deck.d.size should be (2)

    deck.replace(card)
    deck.d.size should be (3)
  }

  it should "play a unit on the board" in {
    val f = fixture
    val deck = new Deck(f.cards)
    val s = new Sim(deck)

    // The fixture guarantees we draw a unit in 7 cards
    s.hand foreach { c =>
      if (c.generic_type == "u") {
        s.play(c)
        s.board.size should be > 0
      }
    }
  }

  it should "play a power in the pool" in {
    val f = fixture
    val deck = new Deck(f.cards)
    val s = new Sim(deck)

    // The fixture guarantees we draw a power in 7 cards
    s.hand foreach { c =>
      if (c.generic_type == "p") {
        s.play(c)
        s.pool.size should be > 0
      }
    }
  }
}
