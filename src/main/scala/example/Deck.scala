package example

import scala.util.Random
import scala.collection.mutable.ListBuffer

/**
  * Deck object which holds a list of cards.
  */
class Deck(val deck: ListBuffer[Card]) {

  var d: ListBuffer[Card] = deck

  // Convenience method for removing a card.
  def draw() : Card = {
    return d.remove(0)
  }

  // Convenience method for putting back a card.
  def replace(card: Card) : Unit = {
    d += card
  }

  // Utilize Scala's shuffle method to shuffle the deck.
  def shuffle() {
    d = Random.shuffle(d)
  }
}
