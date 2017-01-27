package example

import org.scalatest._

class PlayerSpec extends FlatSpec with Matchers {
  "The Player object" should "have a name and default health" in {
    val p = new Player("Player One")
    p.name should be ("Player One")
    p.health should be (25)
  }

  it should "have definable health" in {
    val p = new Player("Player One", 50)
    p.name should be ("Player One")
    p.health should not be (25)
    p.health should be (50)
  }

  it should "have adjustable health" in {
    val p = new Player("Player One")
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
}
