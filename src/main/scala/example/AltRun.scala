package example

import akka.actor.{ Props, ActorSystem }
import akka.pattern.gracefulStop
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import example.model.Deck
import example.model.Player
import example.model.Prefab

/**
  * Binary for executing the simulator.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
object AltRun extends App {
  var iterations = 10
  if (args.size > 0) {
    iterations = args(0).toInt
  }

  val playerOne = new Player("Player One", deck = new Deck(Prefab.minionsOfShadowAI()))
  val playerTwo = new Player("Player Two", deck = new Deck(Prefab.studentsOfTimeAI()))

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

    val system = ActorSystem()

    val game = system.actorOf(Props[GameCoordinator])
    val turnLoop = system.actorOf(Props(classOf[AITurn], a, playerOne, playerTwo))
    val simData = new SimData(a, Array(playerOne, playerTwo), turnLoop)

    game ! Setup(simData)
    game ! Begin

    // This weird ass async issue where if I don't wait a bit, the sim cannot finish
    Thread.sleep(5000)
    try {
      val stoppedLoop: Future[Boolean] = gracefulStop(turnLoop, 2 seconds)
      Await.result(stoppedLoop, 3 seconds)
      println("Game Loop stopped")
      val stopped: Future[Boolean] = gracefulStop(game, 2 seconds)
      Await.result(stopped, 3 seconds)
      println("Game stopped")
    } catch {
      case e: Exception => e.printStackTrace
    } finally {
      system.shutdown
    }

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

