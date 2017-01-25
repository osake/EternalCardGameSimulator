package example

import scala.collection.mutable.ListBuffer

// TODO(jfrench): Probably want a configuration file which defines deck size, etc.
// The tests would want to respec the size so I don't need 75 card decks in all the tests :)
class Sim(val d: Deck) {
  var power: Int = 0
  var max_power: Int = 0
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

  def mulligan() {
    mulligan_counter += 1
    // TODO(jfrench): put cards back in deck and shuffle
    reset_and_draw()
    val power_count = decideMulligan("p", hand)
    if (power_count < 2 || power_count > 5) {
      mulligan()
    }
  }

  private def reset_and_draw() {
    hand foreach { c =>
      d.d += c
      hand -= c
    }
    d.shuffle
    draw(7)
  }

  def restart() {
    max_power = 0
    power = 0
    mulligan_counter = 0
    // TODO(jfrench): empty all the card pools back into deck
    reset_and_draw()

  }

  def play() {

  }

  def discard() {
  }

  def hand_data() {
  }

  def decideMulligan(t: String, list: ListBuffer[Card]) : Int = {
    return list.filter(c => c.generic_type == t).size
  }
}
