package example

import scala.util.Random
import scala.collection.mutable.ListBuffer

/**
  * Deck object which holds a list of cards.
  */
class Deck(var cards: ListBuffer[Card]) {

  // Convenience method for removing a card.
  def draw() : Card = {
    return cards.remove(0)
  }

  // Convenience method for putting back a card.
  def replace(card: Card) : Unit = {
    cards += card
  }

  // Utilize Scala's shuffle method to shuffle the deck.
  def shuffle() {
    cards = Random.shuffle(cards)
  }

  def size() : Int = {
    return cards.size
  }
}
