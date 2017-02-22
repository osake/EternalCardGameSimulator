package example

import scala.util.Random
import scala.collection.mutable.ListBuffer

/**
  * Deck object which holds a list of cards.
  */
class Deck(var cards: ListBuffer[Card]) {

  // Convenience method for removing a card.
  def draw() : Option[Card] = {
    if (cards.size > 0) {
      return Some(cards.remove(0))
    } else {
      return None
    }
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
