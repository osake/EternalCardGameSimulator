package example

import scala.util.Random
import scala.collection.mutable.ListBuffer

class Deck(val deck: ListBuffer[Card]) {

  var d: ListBuffer[Card] = deck

  def draw() : Card = {
    return d.remove(0)
  }

  def replace(card: Card) : Unit = {
    d += card
  }

  def shuffle() {
    d = Random.shuffle(d)
  }
}
