package example

import scala.collection.mutable.ListBuffer

class Player(var name: String, var health: Int = 25, var deck: Deck) {
  val MAX_HEALTH = 999
  val MIN_HEALTH = -MAX_HEALTH

  var hand: ListBuffer[Card] = new ListBuffer[Card]

  /**
    * Accounts for taking damage -amount or gaining life +amount.
    */
  def adjustHealth(amount: Int) {
    health += amount
    if (health < MIN_HEALTH) health = MIN_HEALTH
    else if (health > MAX_HEALTH) health = MAX_HEALTH
  }
}
