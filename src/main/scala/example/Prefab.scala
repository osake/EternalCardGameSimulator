package example

import scala.collection.mutable.ListBuffer
import com.google.gson.Gson

object Prefab {

  private val P = "p"
  private val U = "u"
  private val S = "s"
  private val A = "a"

  def testCard() : Card = {
    val gson = new Gson
    val jsonString = """
      {
        "set": "Set1",
        "num": 335,
        "name": "Knight-Chancellor Siraf",
        "rules_text": "Overwhlem; Pay 8 to play a random {T} or {J} unit and double its Attack / Health.",
        "influence": "{T}{J}",
        "id": 376,
        "rarity": "Legendary",
        "colors": [ "Time", "Justice" ],
        "cost": 3,
        "health": 4,
        "subtypes": [ "Soldier" ],
        "attack": 3,
        "generic_type": "Unit",
        "draftable": true,
        "rank": 4
      }
    """
    return gson.fromJson(jsonString.trim, classOf[Card])
  }


  def gauntlet_thirty_deck() : ListBuffer[Card] = {
    var d = new ListBuffer[Card]()

    for (i <- 1 to 30) yield { d += new Card(generic_type = P, cost = 0) }
    for (i <- 1 to 4) yield { d += new Card(generic_type = U, cost = 1) }
    for (i <- 1 to 6) yield { d += new Card(generic_type = U, cost = 2) }
    for (i <- 1 to 8) yield { d += new Card(generic_type = U, cost = 3) }
    for (i <- 1 to 5) yield { d += new Card(generic_type = U, cost = 4) }
    for (i <- 1 to 2) yield { d += new Card(generic_type = U, cost = 5) }
    for (i <- 1 to 10) yield { d += new Card(generic_type = S, cost = 1) }
    for (i <- 1 to 10) yield { d += new Card(generic_type = A, cost = 1) }

    return d
  }
}
