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
case class SimData(sim: Sim, players: Array[Player], gameLoop: ActorRef) extends GameData

// Events
sealed trait GameEvent
case object GameWaiting extends GameEvent
case object EndGame extends GameEvent
case class Setup(simData: SimData) extends GameEvent

class GameCoordinator extends Actor with FSM[State, GameData] with GameState {

  startWith(NotStarted, UninitializedGame)

  when(NotStarted) {
    case Event(Setup(simData), _) =>
      goto(Begin) using simData.copy(simData.sim, simData.players, simData.gameLoop)
    case Event(EndGame, _) =>
      goto(GameOver)
    case _ => stay replying(GameWaiting)
  }

  when(Begin) {
    case Event(Begin, SimData(sim, players, gameLoop)) =>
      println(s"${sim.activePlayer.name} is active")
      gameLoop ! Start
      //Thread.sleep(100)

      gameLoop ! ResetForTurn
      //Thread.sleep(100)

      // Play a power if one is available
      gameLoop ! PlayPower
      //Thread.sleep(100)

      gameLoop ! Combat
      //Thread.sleep(100)
      gameLoop ! Fight
      //Thread.sleep(100)

      gameLoop ! SecondMain
      //Thread.sleep(100)
      gameLoop ! PlayUnit
      //Thread.sleep(100)

      gameLoop ! End
      //Thread.sleep(100)
      gameLoop ! EndTurn
      //Thread.sleep(100)

      stay replying(GameWaiting)
    case Event(EndGame, _) =>
      goto(GameOver) using stateData
    case Event(e, s) =>
      println(s"WOOOOOOO ${e}")
      stay
  }

  when(GameOver) {
    case Event(_, SimData(sim, players, gameLoop))  =>
      println("So long simulator")
      if (sim.playerOne.health < 1) println(s"${sim.playerTwo.name} wins! ${sim.playerTwo.first}")
      if (sim.playerTwo.health < 1) println(s"${sim.playerOne.name} wins! ${sim.playerOne.first}")
      stay
  }

  initialize
}


