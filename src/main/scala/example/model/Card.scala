package example.model

import scala.collection.JavaConversions._
import com.google.gson.Gson

/**
  * Card is the base object for building decks, etc.  This contains all the interesting properties of a card.
  * Maybe, we could introspect on DB columns or something rather than define the schema here, or even read
  * this from a protobuf or the likes.
  *
  * Attributes idea from card data based on github.com/rpaddock/eternaldraft_backend/blob/master/data/cards.json:
  */
class Card(
    val id: Int = -1,
    val set: String = "",
    val num: Int = -1,
    val rarity: String = "",

    val name: String = "",
    val rules_text: String = "",
    val flavor_text: String = "",
    val artist: String = "",

    val cost: Int = -1,
    val colors: Array[String] = Array(),
    val influence: String = "",

    val generic_type: String = "",
    val subtypes: Array[String] = Array(),

    val attack: Int = -1,
    val health: Int = -1,

    // Draft specific attributes
    val draftable: Boolean = false,
    val rank: Int = -1) {

  // Units come into play sick, so let's just force that.
  // TODO(jfrench): Add check for Charge ability when we parse rules text
  var summonSickness = true

  // More transient attributes
  var exhausted = false
  var attacking = false
  var blocking = false
  var blocked = false

  def toJson() = new Gson().toJson(this)

  /**
   * Print the card and cost.
   */
  def showCard() {
    print(s"${name} (${cost}),  ")
  }
}

// TODO (jfrench): Thinking of case classes since there are some differences in how each of these behave
/*
case class Curse() extends Card
case class FastSpell() extends Card
case class Power() extends Card
case class Relic() extends Card
case class RelicWeapon() extends Card
case class Spell() extends Card
case class Unit() extends Card
case class Weapon() extends Card
*/

