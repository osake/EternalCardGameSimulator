package example

import org.scalatest._
import scala.collection.mutable.ListBuffer
import scala.language.reflectiveCalls

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
      var deck = new Deck(cards)
    }

  def anotherFixture =
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
      var deck = new Deck(cards)
    }

  "The Sim" should "initialize a game of cards" in {
    val f = fixture
    val g = anotherFixture
    val s = new Sim(new Player("Bob", 25, f.deck), new Player("Sue", 25, g.deck))

    s.playerOne.hand.size should be (7)
    s.playerOne.deck.deck.size should be (3)

    val card = s.playerOne.deck.draw
    s.playerOne.deck.deck.size should be (2)

    s.playerOne.deck.replace(card)
    s.playerOne.deck.deck.size should be (3)
  }

  it should "play a unit on the board" in {
    val f = fixture
    val g = anotherFixture
    val s = new Sim(new Player("Bob", 25, f.deck), new Player("Sue", 25, g.deck))

    s.playerOne.maxPower    = 5  // force power to a medium value
    s.playerOne.currentPower = 5

    // The fixture guarantees we draw a unit in 7 cards
    s.playerOne.hand foreach { c =>
      if (c.generic_type == "u") {
        s.playerOne.play(c)
        s.playerOne.board.size should be > 0
      }
    }
  }

  it should "play a power in the pool" in {
    val f = fixture
    val g = anotherFixture
    val s = new Sim(new Player("Bob", 25, f.deck), new Player("Sue", 25, g.deck))

    // The fixture guarantees we draw a power in 7 cards
    s.playerOne.hand foreach { c =>
      if (c.generic_type == "p") {
        s.playerOne.play(c)
        s.playerOne.pool.size should be > 0
      }
    }
  }

  it should "not play a card you don't have" in {
    val f = fixture
    val g = anotherFixture
    val trickCard = new Card(generic_type = "s", cost = 99)
    val s = new Sim(new Player("Bob", 25, f.deck), new Player("Sue", 25, g.deck))

    s.playerOne.play(trickCard)
    s.playerOne.hand.size should be (7)
  }

  it should "not discard a card you don't have" in {
    val f = fixture
    val g = anotherFixture
    val trickCard = new Card(generic_type = "s", cost = 99)
    val deck = new Deck(f.cards)
    val s = new Sim(new Player("Bob", 25, f.deck), new Player("Sue", 25, g.deck))

    s.playerOne.discard(trickCard)
    s.playerOne.hand.size should be (7)
  }

  it should "not be able to play an expensive unit without sufficient power" in {
    val f = fixture
    val g = anotherFixture
    val trickCard = new Card(generic_type = "u", cost = 99)
    val s = new Sim(new Player("Bob", 25, f.deck), new Player("Sue", 25, g.deck))
    s.playerOne.hand += trickCard // force expensive card into hand
    s.playerOne.maxPower     = 5  // force power to a medium value
    s.playerOne.currentPower = 5

    s.playerOne.play(trickCard)
    s.playerOne.board should be ('empty)
    s.playerOne.hand.size should be (8)
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
