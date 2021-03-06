package example

import org.scalatest._
import scala.collection.mutable.ListBuffer
import example.model.Card
import example.model.Deck
import example.model.Player

class PlayerSpec extends FlatSpec with Matchers {

  def fixture =
    new {
      var cards = new ListBuffer[Card]()
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
      var deck = new Deck(cards)
    }


  "The Player object" should "have a name and default health" in {
    val f = fixture
    val p = new Player("Player One", deck = f.deck)
    p.name should be ("Player One")
    p.health should be (25)
  }

  it should "have definable health" in {
    val f = fixture
    val p = new Player("Player One", 50, f.deck)
    p.name should be ("Player One")
    p.health should not be (25)
    p.health should be (50)
  }

  it should "have adjustable health" in {
    val f = fixture
    val p = new Player("Player One", deck = f.deck)
    p.name should be ("Player One")

    p.adjustHealth(25)
    p.health should be (50)

    p.adjustHealth(-20)
    p.health should be (30)

    p.adjustHealth(+2500)
    p.health should be (999)

    p.adjustHealth(-2500)
    p.health should be (-999)
  }

  it should "sort power" in {
    val f = fixture
    val p = new Player("Player One", deck = f.deck)
    p.draw(7)
    p.draw(3)
    p.sortPower
    val firstFourCards = p.hand.slice(0, 3)
    firstFourCards foreach { c =>
      c.generic_type should be ("Power")
    }
  }
}
