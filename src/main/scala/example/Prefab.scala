package example

import scala.collection.mutable.ListBuffer

object Prefab {

  private val P = "p"
  private val U = "u"
  private val S = "s"
  private val A = "a"

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
