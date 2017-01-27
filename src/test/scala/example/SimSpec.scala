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

    s.maxPower    = 5  // force power to a medium value
    s.currentPower = 5

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

  it should "not play a card you don't have" in {
    val f = fixture
    val trickCard = new Card("s", 99)
    val deck = new Deck(f.cards)
    val s = new Sim(deck)

    s.play(trickCard)
    s.hand.size should be (7)
  }

  it should "not discard a card you don't have" in {
    val f = fixture
    val trickCard = new Card("s", 99)
    val deck = new Deck(f.cards)
    val s = new Sim(deck)

    s.discard(trickCard)
    s.hand.size should be (7)
  }

  it should "not be able to play an expensive unit without sufficient power" in {
    val f = fixture
    val trickCard = new Card("u", 99)
    val deck = new Deck(f.cards)
    val s = new Sim(deck)
    s.hand += trickCard // force expensive card into hand
    s.maxPower     = 5  // force power to a medium value
    s.currentPower = 5

    s.play(trickCard)
    s.board should be ('empty)
    s.hand.size should be (8)
  }
}
