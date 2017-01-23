package example

import scala.util.Random
import scala.collection.mutable.ListBuffer

class Deck(val deck: ListBuffer[Card]) {

  var d: ListBuffer[Card] = deck

  def draw() {

  }

  def shuffle() {
    d = Random.shuffle(d)
  }
}
