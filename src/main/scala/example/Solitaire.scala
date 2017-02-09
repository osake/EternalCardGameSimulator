package example

import scala.collection.mutable.ListBuffer
import scala.io.StdIn.{readLine,readChar}

/**
  * Binary for executing the simulator in Solitaire mode.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
object Solitaire extends App {
  var iterations = 10
  if (args.size > 0) {
    iterations = args(0).toInt
  }

  val playerOneName = readLine("What name do you want to go by? ")

  // For now, we'll just give you the P1 default deck.
  val playerOne = new Player(playerOneName, deck = new Deck(Prefab.minionsOfShadowAI()), human = true)
  val playerTwo = new Player("Player Two", deck = new Deck(Prefab.studentsOfTimeAI()))

  (1 to iterations) map { index =>
    val a = new Sim(playerOne, playerTwo)
    a.start
    a.activePlayer = a.coinToss
    a.activePlayer.first = true

    println(a.activePlayer.name + " goes first!")

    // Both players determine mulligan, we'll go aggressive and simple for the AI
    playerOne.showHand
    print("Do you wish to mulligan? (y/n) ")
    val playerOneMulligan = readChar()
    if (playerOneMulligan == 'y') {
      playerOne.mulligan
      println(s"${playerOne.name} mulliganed.")
    } else println(s"${playerOne.name} is keeping their hand.")

    val p2PowerCount = playerTwo.countType("Power", playerTwo.hand)
    if (p2PowerCount <  2 || p2PowerCount > 5) {
      println(s"${playerTwo.name} mulliganed.")
      playerTwo.mulligan()
    }

    val gameLoop = new SolitaireTurn(a, playerOne, playerTwo)
    gameLoop.run

    // Output the board states
    a.outputGameState
    a.printChar("~")
    a.playerOne.hand_data
    a.playerTwo.hand_data
    a.printChar("~")

    // Output the winner's name.  TODO(jfrench): Maybe this should happen in gameOver/cleanup

    // End the simulation
    println("Game over!")
    a.gameOver()
  }
}

