package example

import org.scalatest._
import scala.collection.mutable.ListBuffer


class SimSpec extends FlatSpec with Matchers {

  def fixture =
    new {
      var cards = new ListBuffer[Card]()
      cards += (
        new Card(generic_type = "p", cost = 0),
        new Card(generic_type = "p", cost = 0),
        new Card(generic_type = "u", cost = 2),
        new Card(generic_type = "u", cost = 3),
        new Card(generic_type = "s", cost = 1),
        new Card(generic_type = "p", cost = 0),
        new Card(generic_type = "p", cost = 0),
        new Card(generic_type = "u", cost = 2),
        new Card(generic_type = "u", cost = 3),
        new Card(generic_type = "s", cost = 1)
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
    val trickCard = new Card(generic_type = "s", cost = 99)
    val deck = new Deck(f.cards)
    val s = new Sim(deck)

    s.play(trickCard)
    s.hand.size should be (7)
  }

  it should "not discard a card you don't have" in {
    val f = fixture
    val trickCard = new Card(generic_type = "s", cost = 99)
    val deck = new Deck(f.cards)
    val s = new Sim(deck)

    s.discard(trickCard)
    s.hand.size should be (7)
  }

  it should "not be able to play an expensive unit without sufficient power" in {
    val f = fixture
    val trickCard = new Card(generic_type = "u", cost = 99)
    val deck = new Deck(f.cards)
    val s = new Sim(deck)
    s.hand += trickCard // force expensive card into hand
    s.maxPower     = 5  // force power to a medium value
    s.currentPower = 5

    s.play(trickCard)
    s.board should be ('empty)
    s.hand.size should be (8)
  }

  it should "not test this function, but I'm lazy" in {
    val c = Prefab.testCard
    c.cost should be (3) // because we know stuff
  }

  it should "not test this function either, but I'm lazy" in {
    val c = Prefab.testCollection
    c.size should be > 480 // because we know more stuff
  }
}
