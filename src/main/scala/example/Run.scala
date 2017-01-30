package example

import scala.collection.mutable.ListBuffer

/**
  * Binary for executing the simulator.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
object Run extends Greeting with App {
  var iterations = 10
  if (args.size > 0) {
    iterations = args(0).toInt
  }

  val playerOne = new Player("Player One", deck = new Deck(Prefab.gauntlet_thirty_deck()))
  val playerTwo = new Player("Player Two", deck = new Deck(Prefab.gauntlet_thirty_deck()))

  val f = new File("data/cards.json")
  //println(f.contents)

  println(greeting)
  (1 to iterations) map { index =>
    val a = new Sim(playerOne, playerTwo)

    val p1PowerCount = playerOne.countType("p", playerOne.hand)
    if (p1PowerCount <  2 || p1PowerCount > 5) {
      playerOne.mulligan()
    }

    val p2PowerCount = playerTwo.countType("p", playerTwo.hand)
    if (p2PowerCount <  2 || p2PowerCount > 5) {
      playerTwo.mulligan()
    }
  }
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
