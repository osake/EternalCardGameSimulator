package example.model

import scala.collection.mutable.ListBuffer
import com.google.gson.Gson

object Prefab {

  private val P = "Power"
  private val U = "Unit"
  private val S = "Spell"
  private val A = "Attachment"

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

  def testCollection() : Array[Card] = {
    val gson = new Gson
    val jsonString = new File("data/cards.json").contents

    return gson.fromJson(jsonString.trim, classOf[Array[Card]])
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

  def simpleDeck() : ListBuffer[Card] = {
    var d = new ListBuffer[Card]()
    val sampleCollection = testCollection

    for (i <- 1 to 30) yield { d += sampleCollection.filter(c => c.name == "Primal Sigil").head }
    for (i <- 1 to 45) yield { d += sampleCollection.filter(c => c.name == "Cloudsnake Hatchling").head }
    println("Deck size: " + d.size)

    d
  }


  def minionsOfShadowAI() : ListBuffer[Card] = {
    var d = new ListBuffer[Card]()
  val sampleCollection = testCollection

    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Direfang Spider").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Direwood Rampager").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Xenan Destroyer").head }
    for (i <- 1 to 4) yield { d += sampleCollection.filter(c => c.name == "Devouring Shadow").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Dark Wisp").head }
    for (i <- 1 to 4) yield { d += sampleCollection.filter(c => c.name == "Sporefolk").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Dark Return").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Cabal Cutthroat").head }
    for (i <- 1 to 4) yield { d += sampleCollection.filter(c => c.name == "Vara's Favor").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Scavenging Vulture").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Back-Alley Bouncer").head }
    d += sampleCollection.filter(c => c.name == "Plague").head
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Suffocate").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Spirit Drain").head }
    for (i <- 1 to 37) yield { d += sampleCollection.filter(c => c.name == "Shadow Sigil").head }

    d
  }

  def studentsOfTimeAI() : ListBuffer[Card] = {
    var d = new ListBuffer[Card]()
    val sampleCollection = testCollection

    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Silence").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Sand Warrior").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Amber Monument").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Refresh").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Xenan Guardian").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Humbug").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Predator's Instinct").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Timekeeper").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Dormant Sentinel").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Praxis Displacer").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Synchronized Strike").head } // best guess 3x
    for (i <- 1 to 4) yield { d += sampleCollection.filter(c => c.name == "Bold Adventurer").head }
    for (i <- 1 to 3) yield { d += sampleCollection.filter(c => c.name == "Oasis Sanctuary").head }
    for (i <- 1 to 4) yield { d += sampleCollection.filter(c => c.name == "Horned Vorlunk").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Towering Terrazon").head }
    for (i <- 1 to 2) yield { d += sampleCollection.filter(c => c.name == "Initiate of the Sands").head }
    for (i <- 1 to 34) yield { d += sampleCollection.filter(c => c.name == "Time Sigil").head }

    d
  }


  /* Mono-justice stall deck
4 Steadfast Deputy (Set1 #504)
4 Valkyrie Aspirant (Set1 #127)
4 Crownwatch Paladin (Set1 #139)
4 Eager Owlet (Set1 #144)
4 Paladin Oathbook (Set1 #140)
2 Rolant's Favor (Set0 #18)
4 Vanquish (Set1 #143)
3 Brightmace Paladin (Set1 #147)
4 Silverwing Familiar (Set1 #152)
4 Auric Runehammer (Set1 #166)
4 Hammer of Might (Set1 #170)
4 Mantle of Justice (Set0 #21)
2 Throne Warden (Set0 #514)
1 Augmented Form (Set1 #182)
2 Valkyrie Wings (Set1 #181)
25 Justice Sigil (Set1 #126)
  */

}
