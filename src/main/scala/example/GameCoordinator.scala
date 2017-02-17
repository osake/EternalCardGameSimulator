package example

import akka.actor._

sealed trait State
case object NotStarted extends State
case object Begin extends State
case object GameOver extends State

object GameCoordinator {
  case object Waiting
  case object Start
  //case class Setup(sim: Sim, players: Array[Player])
}

sealed trait GameData
case object UninitializedGame extends GameData
case class SimData(sim: Sim, players: Array[Player]) extends GameData

class GameCoordinator extends Actor with FSM[State, Data] {
  startWith(NotStarted, Uninitialized)

  when(NotStarted) {
    case Event(Start, simData) => goto(Begin)
    case _ => stay replying(Waiting)
  }
  when(GameOver) {
    case Event(_, simData) => stay replying(simData)
    }

  initialize
}

