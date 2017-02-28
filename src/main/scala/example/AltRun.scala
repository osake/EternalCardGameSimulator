package example

import akka.actor.{ Props, Actor, ActorSystem }
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
    val system = ActorSystem()
    val coordinator = system.actorOf(Props(classOf[AICoordinator], a, playerOne, playerTwo, system))
  }
}
