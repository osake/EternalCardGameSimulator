package example

import akka.actor._
import akka.pattern.gracefulStop
import org.backuity.ansi.AnsiFormatter.FormattedHelper
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import example.model.Player
import scala.util.Try
import scala.io.StdIn.readLine

// States
sealed trait State
case object NotStarted extends State
case object Begin extends State
case object GameOver extends State

sealed trait GameData
case object UninitializedGame extends GameData
case class SimData(sim: Sim, players: Array[Player], turnLoop: ActorRef) extends GameData

// Events
sealed trait GameEvent
case object GameWaiting extends GameEvent
case object EndGame extends GameEvent
case class Setup(simData: SimData) extends GameEvent

class GameCoordinator extends Actor with FSM[State, GameData] with GameState with PlayerInput {

  var takingTurn: Boolean = _

  startWith(NotStarted, UninitializedGame)

  when(NotStarted) {
    case Event(Setup(simData), _) =>
      goto(Begin) using simData.copy(simData.sim, simData.players, simData.turnLoop)
    case Event(EndGame, _) =>
      goto(GameOver)
    case _ => stay replying(GameWaiting)
  }

  when(Begin) {
    case Event(Begin, SimData(sim, players, turnLoop)) =>
      if (sim.activePlayer.human) takingTurn = true
      while (takingTurn) {
        parseCommand(getCommand(), sim.activePlayer)
      }

      println(s"${sim.activePlayer.name} is active")
      turnLoop ! Start

      turnLoop ! ResetForTurn

      // Play a power if one is available
      turnLoop ! PlayPower

      turnLoop ! Combat
      turnLoop ! Fight

      turnLoop ! SecondMain
      turnLoop ! PlayUnit

      turnLoop ! End
      turnLoop ! EndTurn

      stay replying(GameWaiting)
    case Event(EndGame, _) =>
      goto(GameOver) using stateData
    case Event(e, s) =>
      println(s"WOOOOOOO ${e}")
      stay
  }

  when(GameOver) {
    case Event(_, SimData(sim, players, turnLoop))  =>
      println("So long simulator")
      if (sim.playerOne.health < 1) println(s"${sim.playerTwo.name} wins! ${sim.playerTwo.first}")
      if (sim.playerTwo.health < 1) println(s"${sim.playerOne.name} wins! ${sim.playerOne.first}")
      stay
  }

  initialize

}

trait PlayerInput {
  def getCommand(prompt: String = "Enter a command: ") : String = {
    readLine(prompt)
  }

  /**
   * Convenience "any key" prompt.
   */
  def getAnyKey() {
    getCommand("Press Enter to continue.")
  }

  def parseCommand(command: String, player: Player) {
    command match {
      case "combat" => println("Placeholder for combat")
      case "end" => performEndTurn
      case "exit" => sys.exit(0) // Just exit 0 for now.
      case "hand" => printHand(player)
      case "help" => printHelp
      case "main2" => println("Placeholder for 2nd main phase")
      case "play" => parseCardMenuOptions(player)
      case _ => println(s"You entered: ${command}, but I'm not sure how to handle that command.")
    }
  }

  def performEndTurn() {
    println(ansi"%blue{Ending turn.}")
//    takingTurn = false
  }

  def printHelp() {
    println("This is no help, it's a space station.")
    println("\nCurrent supported commands are: help and exit")
  }

  /**
   * Show the hand of the player.
   */
  def printHand(player: Player) {
    player.showHand
  }

  def parseCardMenuOptions(player: Player) {
    var picked = false
    if (player.hand.size == 0) picked = true // guard against empty hand
    while (!picked) {
      val card = getCommand("Enter the number in square brackets of the card you want to play. ")
      val number = cliToInt(card)
      number match {
        case Some(n) => {
          if (n >= 0 && n < player.hand.size) {
            picked = true
            player.play(player.hand(n))
          } else {
            println(s"You don't have card ${n} in your hand.")
          }
        }
        case None => println("I didn't understand which card.  Try again.")
      }
    }
  }

  def cliToInt(value: String) : Option[Int] = {
    if (value.isEmpty) None else Try(value.toInt).toOption
  }
}
