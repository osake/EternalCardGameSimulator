package example

import akka.actor.{ Props, Actor, ActorSystem }
import akka.pattern.gracefulStop
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.io.StdIn.readLine
import example.model.Deck
import example.model.Player
import example.model.Prefab

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
    val system = ActorSystem()
    val coordinator = system.actorOf(Props(classOf[PcCoordinator], a, playerOne, playerTwo, system))
  }
}
