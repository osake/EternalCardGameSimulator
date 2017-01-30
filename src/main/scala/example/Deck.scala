package example

import scala.util.Random
import scala.collection.mutable.ListBuffer

/**
  * Deck object which holds a list of cards.
  */
class Deck(var deck: ListBuffer[Card]) {

  // Convenience method for removing a card.
  def draw() : Card = {
    return deck.remove(0)
  }

  // Convenience method for putting back a card.
  def replace(card: Card) : Unit = {
    deck += card
  }

  // Utilize Scala's shuffle method to shuffle the deck.
  def shuffle() {
    deck = Random.shuffle(d)
  }
}
