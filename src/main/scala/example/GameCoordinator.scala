package example

import akka.actor._
import akka.pattern.gracefulStop
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

// States
sealed trait State
case object NotStarted extends State
case object Begin extends State
case object GameOver extends State

sealed trait GameData
case object UninitializedGame extends GameData
case class SimData(sim: Sim, players: Array[Player]) extends GameData

// Events
sealed trait GameEvent
case object GameWaiting extends GameEvent
case class Setup(simData: SimData) extends GameEvent

class GameCoordinator extends Actor with FSM[State, Data] {

  startWith(NotStarted, Uninitialized)

  when(NotStarted) {
    case Event(Setup, simData) =>
      val setupSimData = someFunc(simData)
      goto(Begin) using (simData)
    case _ => stay replying(Waiting)
  }

  when(Begin) {
    case _ => stay replying(Waiting)
  }

  when(GameOver) {
    case Event(_, simData) => stay replying(simData)
  }

  initialize

  // Methods that control turn behavior as this class listens to turn messages to control the game

  def someFunc(simData: Data) {
    simData match {
      case s: SimData =>
        val system = ActorSystem()
        val gameLoop = system.actorOf(Props(classOf[AITurn], s.sim, s.players(0), s.players(1)))

        gameLoop ! Start

        try {
          val stopped: Future[Boolean] = gracefulStop(gameLoop, 2 seconds)
          Await.result(stopped, 3 seconds)
          println("Game Loop stopped")
        } catch {
          case e: Exception => e.printStackTrace
        } finally {
          system.shutdown
        }
      case _ => println("No match for Data type")
    }
  }
}


