package example

import akka.actor._

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
    case Event(Setup, simData) => goto(Begin)
    case _ => stay replying(Waiting)
  }
  when(GameOver) {
    case Event(_, simData) => stay replying(simData)
    }

  initialize
}

