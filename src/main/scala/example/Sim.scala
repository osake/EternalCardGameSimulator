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

  d.shuffle
  draw(7)

  def draw(n: Int) {
    for (i <- 1 to 7) yield hand.append(d.draw)
  }
}
