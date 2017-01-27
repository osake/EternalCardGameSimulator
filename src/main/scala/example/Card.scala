package example

/**
  * Card is the base object for building decks, etc.  This contains all the interesting properties of a card.
  * Maybe, we could introspect on DB columns or something rather than define the schema here, or even read
  * this from a protobuf or the likes.
  *
  * Attributes idea from card data based on github.com/rpaddock/eternaldraft_backend/blob/master/data/cards.json:
  */
class Card(
    val id: Int = -1,
    val set: Int = -1,
    val num: Int = -1,
    val rarity: String = "",

    val name: String = "",
    val rules_text: String = "",
    val flavor_text: String = "",
    val artist: String = "",

    val cost: Int = -1,
    val colors: List[String] = List(""),
    val influence: String = "",

    val generic_type: String = "",
    val subtypes: List[String] = List(""),

    val attack: Int = -1,
    val health: Int = -1,

    // Draft specific attributes
    val draftable: Boolean = false,
    val rank: Int = -1)

