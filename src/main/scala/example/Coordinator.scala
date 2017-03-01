package example

import akka.actor.{ Props, Actor, ActorSystem, ActorRef }
import akka.pattern.gracefulStop
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.io.StdIn.readLine
import example.model.Deck
import example.model.Player
import example.model.Prefab

abstract class Coordinator(a: Sim, playerOne: Player, playerTwo: Player, system: ActorSystem) extends Actor{

}

class PcCoordinator(a: Sim, playerOne: Player, playerTwo: Player, system: ActorSystem) extends Coordinator(a, playerOne, playerTwo, system) with GracefulShutdown {
  a.start
  a.activePlayer = a.coinToss
  a.activePlayer.first = true

  println(a.activePlayer.name + " goes first!")

  // Both players determine mulligan, we'll go aggressive and simple for the AI
  playerOne.showHand
  print("Do you wish to mulligan? (y/n) ")
  val playerOneMulligan = readLine()
  if (playerOneMulligan.charAt(0) == 'y') {
    playerOne.mulligan
    println(s"${playerOne.name} mulliganed.")
  } else println(s"${playerOne.name} is keeping their hand.")

  val p2PowerCount = playerTwo.countType("Power", playerTwo.hand)
  if (p2PowerCount <  2 || p2PowerCount > 5) {
    println(s"${playerTwo.name} mulliganed.")
    playerTwo.mulligan()
  }

  val game = system.actorOf(Props(classOf[GameCoordinator], this))
  val turnLoop = system.actorOf(Props(classOf[AITurn], a, playerOne, playerTwo))
  val simData = new SimData(a, Array(playerOne, playerTwo), turnLoop)

  game ! Setup(simData)
  game ! Begin

  def receive = {
    case "done" =>
      systemShutdown(system, turnLoop, game)

      // Output the board states
      a.outputBoardState

      // Output the winner's name.  TODO(jfrench): Maybe this should happen in gameOver/cleanup

      // End the simulation
      println("Game over!")
      a.gameOver()
      context stop self
  }
}

class AICoordinator(a: Sim, playerOne: Player, playerTwo: Player, system: ActorSystem) extends Coordinator(a, playerOne, playerTwo, system) with GracefulShutdown {
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

  val game = system.actorOf(Props(classOf[GameCoordinator], this))
  val turnLoop = system.actorOf(Props(classOf[AITurn], a, playerOne, playerTwo))
  val simData = new SimData(a, Array(playerOne, playerTwo), turnLoop)

  game ! Setup(simData)
  game ! Begin

  def receive = {
    case "done" =>
      systemShutdown(system, turnLoop, game)

      // Output the board states
      a.outputBoardState

      // Output the winner's name.  TODO(jfrench): Maybe this should happen in gameOver/cleanup

      // End the simulation
      println("Game over!")
      a.gameOver()
      context stop self
  }
}

trait GracefulShutdown {
  def systemShutdown(system: ActorSystem, actorRefs: ActorRef*) {
      try {
        for (ref <- actorRefs) yield {
          val stoppedLoop: Future[Boolean] = gracefulStop(ref, 2 seconds)
          Await.result(stoppedLoop, 3 seconds)
          println("Actor stopped stopped")
        }
      } catch {
        case e: Exception => e.printStackTrace
      } finally {
        system.shutdown
      }
  }
}

