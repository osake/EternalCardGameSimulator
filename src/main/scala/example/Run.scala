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

  val playerOne = new Player("Player One", deck = new Deck(Prefab.minionsOfShadowAI()))
  val playerTwo = new Player("Player Two", deck = new Deck(Prefab.simpleDeck()))

  val f = new File("data/cards.json")
  //println(f.contents)

  println(greeting)
  (1 to iterations) map { index =>
    val a = new Sim(playerOne, playerTwo)
    a.start
    a.activePlayer = a.coinToss
    a.activePlayer.first = true

    println(a.activePlayer.name + " goes first!")

    // Both players determine mulligan, we'll go aggressive and simple
    val p1PowerCount = playerOne.countType("Power", playerOne.hand)
    if (p1PowerCount <  2 || p1PowerCount > 5) {
      playerOne.mulligan()
    }

    val p2PowerCount = playerTwo.countType("Power", playerTwo.hand)
    if (p2PowerCount <  2 || p2PowerCount > 5) {
      playerTwo.mulligan()
    }

    var isGameOver = false // TODO(jfrench): We can fix this later to be in simulator or something
    var maxTurns = 10 // Just for brevity... what is the actual limit?
    var turnCounter = 0
    // Now we're ready to play.
    while (!isGameOver) {
      println(a.whoseTurn().name + "'s turn.")
      showHand(a.activePlayer)
      // If the turn counter is 0 and player is first, just in case we decide to extract turnCounter
      // then we skip the draw phase
      if (turnCounter == 0 && a.activePlayer.first) {
        println("You go first... no card for you!")
      } else {
        a.activePlayer.draw(1)
        showCard(a.activePlayer.hand.last)
      }

      println(a.activePlayer.name + " has " + a.activePlayer.hand.size + " cards in hand.")

      // Discard step
      if (a.activePlayer.hand.size > 9) {
        println("Automated discard to mimic max hand of 9 at end of turn")
        println("Discarding: " +  a.activePlayer.discard(a.activePlayer.hand.last).name)
      }

      a.nextPlayer()
      turnCounter += 1 // maybe this makes sense to track on each player

      // Cleanup checks to see if we should set game over.
      isGameOver = turnCounter > maxTurns
    }

    // Output the winner's name.  TODO(jfrench): Maybe this should happen in gameOver/cleanup

    // End the simulation
    println("Game over!")
    a.gameOver()
  }

  def showHand(p: Player) {
    p.hand foreach { card =>
      showCard(card)
    }
  }

  def showCard(card: Card) {
    println(card.name + ", " + card.cost)
  }
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
