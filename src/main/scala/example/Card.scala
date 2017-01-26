package example

/**
  * Card is the base object for building decks, etc.  This contains all the interesting properties of a card.
  */
class Card(val t: String, val c: Int) {
  val generic_type = t
  val cost = c
}
