package example

import org.scalatest._
import scala.collection.mutable.ListBuffer
import scala.language.reflectiveCalls

class SimSpec extends FlatSpec with Matchers with BeforeAndAfterEach {

  var cards = new ListBuffer[Card]()
  var cards2 = new ListBuffer[Card]()
  var deck: Deck = _
  var deck2: Deck = _
  var sim: Sim = _

  override def beforeEach() {
    cards += (
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Unit", cost = 2),
      new Card(generic_type = "Unit", cost = 3),
      new Card(generic_type = "Spell", cost = 1),
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Unit", cost = 2),
      new Card(generic_type = "Unit", cost = 3),
      new Card(generic_type = "Spell", cost = 1)
    )
    cards2 += (
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Unit", cost = 2),
      new Card(generic_type = "Unit", cost = 3),
      new Card(generic_type = "Spell", cost = 1),
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Power", cost = 0),
      new Card(generic_type = "Unit", cost = 2),
      new Card(generic_type = "Unit", cost = 3),
      new Card(generic_type = "Spell", cost = 1)
    )
    deck = new Deck(cards)
    deck2 = new Deck(cards2)
    sim = new Sim(new Player("Bob", 25, deck), new Player("Sue", 25, deck2))
  }

  "The Sim" should "initialize a game of cards" in {
    sim.start

    sim.playerOne.hand.size should be (7)
    sim.playerOne.deck.cards.size should be (3)

    val card = sim.playerOne.deck.draw
    sim.playerOne.deck.cards.size should be (2)

    sim.playerOne.deck.replace(card)
    sim.playerOne.deck.cards.size should be (3)
  }

  it should "play a unit on the board" in {
    sim.start

    sim.playerOne.maxPower    = 5  // force power to a medium value
    sim.playerOne.currentPower = 5

    // The fixture guarantees we draw a unit in 7 cards
    sim.playerOne.hand foreach { c =>
      if (c.generic_type == "Unit") {
        sim.playerOne.play(c)
        sim.playerOne.board.size should be > 0
      }
    }
  }

  it should "play a power in the pool" in {
    sim.start

    // The fixture guarantees we draw a power in 7 cards
    sim.playerOne.hand foreach { c =>
      if (c.generic_type == "Power") {
        sim.playerOne.play(c)
        sim.playerOne.pool.size should be > 0
      }
    }
  }

  it should "not play a card you don't have" in {
    val trickCard = new Card(generic_type = "Spell", cost = 99)
    sim.start

    sim.playerOne.play(trickCard)
    sim.playerOne.hand.size should be (7)
  }

  it should "not discard a card you don't have" in {
    val trickCard = new Card(generic_type = "Spell", cost = 99)
    sim.start

    sim.playerOne.discard(trickCard)
    sim.playerOne.hand.size should be (7)
  }

  it should "not be able to play an expensive unit without sufficient power" in {
    val trickCard = new Card(generic_type = "Unit", cost = 99)
    sim.start

    sim.playerOne.hand += trickCard // force expensive card into hand
    sim.playerOne.maxPower     = 5  // force power to a medium value
    sim.playerOne.currentPower = 5

    sim.playerOne.play(trickCard)
    sim.playerOne.board should be ('empty)
    sim.playerOne.hand.size should be (8)
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
