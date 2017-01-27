package example

import scala.collection.mutable.ListBuffer

// TODO(jfrench): Probably want a configuration file which defines deck size, etc.
// The tests would want to respec the size so I don't need 75 card decks in all the tests :)
/**
  * Sim is the heart of the simulator.  It's effectively the methods needed to play a game of cards.
  * It utilizes decks and cards, then extends methods such as mulligans, playing, and restarting.
  * A binary which utilizes the simulator will just utilize these common terms to navigate the sim
  * so that it can produce output based on given inputs.
  */
class Sim(val d: Deck) {
  var power: Int = 0
  var maxPower: Int = 0
  var currentPower: Int = 0
  var mulligan_counter: Int = 0
  var hand: ListBuffer[Card] = new ListBuffer[Card]()
  var pool: ListBuffer[Card] = new ListBuffer[Card]()
  var board: ListBuffer[Card] = new ListBuffer[Card]()
  var v: ListBuffer[Card] = new ListBuffer[Card]()

  // Setup the simulator with a shuffled deck and a hand of cards.
  d.shuffle
  draw(7)

  def getMulliganCounter() : Int = {
    return mulligan_counter
  }

  def draw(n: Int) {
    for (i <- 1 to n) yield hand.append(d.draw)
  }

  /**
    * Mulligan is when you're not happy with a hand.  In this simulator, it is
    * specific to having 2-5 power which is a game requirement.  We also track
    * how many mulligans happen when the player initiates one.  In general testing,
    * about 1 in 1,000,000 times you would see 8 redraws.
    */
  def mulligan() {
    mulligan_counter += 1
    // TODO(jfrench): put cards back in deck and shuffle
    reset_and_draw()
    val power_count = countType("p", hand)
    if (power_count < 2 || power_count > 5) {
      mulligan()
    }
  }

  /**
    * While private, this is a useful method for cleaning up the state.  Reset all
    * the lists by replacing them into the deck, shuffle, and draw seven new cards.
    */
  private def reset_and_draw() {
    hand foreach { c =>
      d replace c
      hand -= c
    }
    v foreach { c =>
      d replace c
      v -= c
    }
    board foreach { c =>
      d replace c
      board -= c
    }
    pool foreach { c =>
      d replace c
      pool -= c
    }
    d.shuffle
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
        case "p" => playPower(c)
        case "u" => playUnit(c)
        case "s" => playSpell(c)
        case "a" => playAttachment(c)
        case _ => println("Unexpected case: " + _)
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
      println("Could not afford " + c.cost + " card with " + currentPower + " power available.")
    }
  }

  private def playSpell(c: Card) {
    // TODO(jfrench): Add spell interactions
    if (isAffordable(c)) {
      hand -= c
      currentPower -= c.cost
      v += c
    } else {
      println("Could not afford " + c.cost + " card with " + currentPower + " power available.")
    }
  }

  private def playAttachment(c: Card) {
    // TODO(jfrench): Add attachment interactions
    if (isAffordable(c)) {
      hand -= c
      currentPower -= c.cost
      board += c // temporarily just chuck it on the board
    } else {
      println("Could not afford " + c.cost + " card with " + currentPower + " power available.")
    }
  }

  private def isAffordable(c: Card) : Boolean = {
    return c.cost <= currentPower
  }

  def discard(c: Card) {
    if (hand.contains(c)) {
      hand -= c
      v += c
    } else {
      println("You can't discard a card you don't have.")
    }
  }

  def hand_data() {
    println("P: " + countType("p", hand))
    println("U: " + countType("u", hand))
    println("S: " + countType("s", hand))
    println("A: " + countType("a", hand))
    println("Mulligans: " + mulligan_counter)
  }

  /**
    * Given a list and generic type, get a count of those cards from the list.
    */
  def countType(t: String, list: ListBuffer[Card]) : Int = {
    return list.filter(c => c.generic_type == t).size
  }
}

