package example

import com.google.gson.Gson
import com.typesafe.scalalogging.LazyLogging
import scala.collection.mutable.ListBuffer

/**
 * Represent a player.  By default, you must present a name and a deck.  The health and human settings
 * are defaulted to 25 and false respectively.  Meaning the players are standard AI units.
 */
class Player(var name: String, var health: Int = 25, var deck: Deck, var human: Boolean = false) extends LazyLogging {
  val MAX_HEALTH = 999
  val MIN_HEALTH = -MAX_HEALTH

  var power: Int = 0
  var maxPower: Int = 0
  var currentPower: Int = 0
  var mulligan_counter: Int = 0
  var hand: ListBuffer[Card] = new ListBuffer[Card]()
  var pool: ListBuffer[Card] = new ListBuffer[Card]()
  var board: ListBuffer[Card] = new ListBuffer[Card]()
  var v: ListBuffer[Card] = new ListBuffer[Card]()
  var first: Boolean = false

  /**
    * Accounts for taking damage -amount or gaining life +amount.
    */
  def adjustHealth(amount: Int) {
    health += amount
    if (health < MIN_HEALTH) health = MIN_HEALTH
    else if (health > MAX_HEALTH) health = MAX_HEALTH
  }

  def draw(n: Int) {
    if (deck.size >= n) {
      for (i <- 1 to n) yield hand.append(deck.draw.get)
    }
  }

  def getMulliganCounter() : Int = {
    return mulligan_counter
  }

  /**
    * Mulligan is when you're not happy with a hand.  In this simulator, it is
    * specific to having 2-5 power which is a game requirement.  We also track
    * how many mulligans happen when the player initiates one.  In general testing,
    * about 1 in 1,000,000 times you would see 8 redraws.
    */
  def mulligan() {
    mulligan_counter += 1
    reset_and_draw()
    val power_count = countType("Power", hand)
    if (power_count < 2 || power_count > 5) {
      mulligan()
    }
  }

  /**
    * While private, this is a useful method for cleaning up the state.  Reset all
    * the lists by replacing them into the deck, shuffle, and draw seven new cards.
    */
  private def reset_and_draw() {
    // TODO(jfrench): Let's replace this with a simpler reset:
    //   store original deck in init, then empty list buffers  and set deck = originalDeck
    hand foreach { c =>
      deck replace c
      hand -= c
    }
    v foreach { c =>
      deck replace c
      v -= c
    }
    board foreach { c =>
      deck replace c
      board -= c
    }
    pool foreach { c =>
      deck replace c
      pool -= c
    }
    deck.shuffle
    draw(7)
  }

  /**
    * Restarting a game, so setting everything back to zero and resetting board state.
    */
  def restart() {
    maxPower = 0
    power = 0
    mulligan_counter = 0
    reset_and_draw()
  }

  /**
    * Handle the various behavior of card types when they're played.
    */
  def play(c: Card) {
    if (hand.contains(c)) {
      c.generic_type match {
        case "Power" => playPower(c)
        case "Unit" => playUnit(c)
        case "Spell" => playSpell(c)
        case "Fast Spell" => playFastSpell(c)
        case "Attachment" => playAttachment(c)
        case _ => logger.warn("Unexpected generic type" + c.generic_type)
      }
    } else {
      println("You can't play a card you don't have.")
    }
  }

  private def playPower(c: Card) {
    hand -= c
    pool += c
    power += 1
    maxPower += 1
    currentPower += 1
  }

  private def playUnit(c: Card) {
    if (isAffordable(c)) {
      hand -= c
      currentPower -= c.cost
      board += c
    } else {
      logger.warn("Could not afford " + c.cost + " unit with " + currentPower + " power available.")
    }
  }

  private def playSpell(c: Card) {
    // TODO(jfrench): Add spell interactions
    if (isAffordable(c)) {
      hand -= c
      currentPower -= c.cost
      v += c
    } else {
      logger.warn("Could not afford " + c.cost + " spell with " + currentPower + " power available.")
    }
  }

  private def playFastSpell(c: Card) {
    // TODO(jfrench): Add spell interactions
    if (isAffordable(c)) {
      hand -= c
      currentPower -= c.cost
      v += c
    } else {
      logger.warn("Could not afford " + c.cost + " fast spell with " + currentPower + " power available.")
    }
  }

  private def playAttachment(c: Card) {
    // TODO(jfrench): Add attachment interactions
    if (isAffordable(c)) {
      hand -= c
      currentPower -= c.cost
      board += c // temporarily just chuck it on the board
    } else {
      logger.warn("Could not afford " + c.cost + " attachment with " + currentPower + " power available.")
    }
  }

  private def isAffordable(c: Card) : Boolean = {
    return c.cost <= currentPower
  }

  def discard(c: Card) : Card = {
    if (hand.contains(c)) {
      hand -= c
      v += c
    } else {
      logger.warn("You can't discard a card you don't have.")
    }
    c // Let us know what card we dumped.
  }

  def hand_data() {
    println("P: " + countType("Power", hand))
    println("U: " + countType("Unit", hand))
    println("S: " + countType("Spell", hand))
    println("A: " + countType("Attachment", hand))
    println("Mulligans: " + mulligan_counter)
  }

  /**
    * Given a list and generic type, get a count of those cards from the list.
    */
  def countType(t: String, list: ListBuffer[Card]) : Int = {
    return list.filter(c => c.generic_type == t).size
  }

  /**
   * Push all the power cards to the head of the list so you can determine mulligan more easily.
   */
  def sortPower() {
    val pc = hand.filter(c => c.generic_type == "Power")
    hand --= pc
    hand.++=:(pc)
  }

  /**
   * Print the hand.
   */
  def showHand() {
    print(s"${name}'s hand: ")
    hand.zipWithIndex foreach { case(card, i) =>
      print(s"[${i}]:")
      card.showCard
    }
    print("\n")
  }
}
