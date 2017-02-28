package example

import akka.actor._
import akka.pattern.gracefulStop
import org.backuity.ansi.AnsiFormatter.FormattedHelper
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import example.model.Player
import scala.util.Try
import scala.io.StdIn.readLine

import org.joda.time.DateTime

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

class GameCoordinator(runner: Actor)  extends Actor with FSM[State, GameData] with GameState with PlayerInput {

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
      if (sim.activePlayer.human) {
        println("human player")
        println(s"${sim.activePlayer.name} is active")

        turnLoop ! Start
        turnLoop ! ResetForTurn

        takingTurn = true
        var startTime = DateTime.now
        while (takingTurn) {
          parseCommand(getCommand(), sim.activePlayer, turnLoop)
          // Ghetto version of a 30 second timer
          if (DateTime.now.getMillis - startTime.getMillis > 30000) {
            println("Time's up!")
            takingTurn = false
            sender ! "done" // shim to exit the sim for now
          }
          startTime = DateTime.now // I think this will work since we're just timing the parseCommand
        }
        println("sending an endturn")
        turnLoop ! EndTurn
      } else { // AI Turn
        println("AI player")

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
      }

      stay replying(GameWaiting)
    case Event(EndGame, _) =>
      goto(GameOver) using stateData
    case Event(e, s) =>
      println(s"WAAAAAAAA ${e}")
      stay
  }

  when(GameOver) {
    case Event(_, SimData(sim, players, turnLoop))  =>
      println("So long simulator")
      if (sim.playerOne.health < 1) println(s"${sim.playerTwo.name} wins! ${sim.playerTwo.first}")
      if (sim.playerTwo.health < 1) println(s"${sim.playerOne.name} wins! ${sim.playerOne.first}")
      runner.self ! "done" // shim to exit the sim for now
      println(sender.toString)
      println(turnLoop.toString)
      println("headin' out")
      turnLoop ! EndTurn
      stay
  }

  initialize

}

trait PlayerInput {

  var takingTurn: Boolean = _

  def getCommand(prompt: String = "Enter a command: ") : String = {
    readLine(prompt)
  }

  /**
   * Convenience "any key" prompt.
   */
  def getAnyKey() {
    getCommand("Press Enter to continue.")
  }

  /**
   * For the placeholder sets, we'll just run the AI version of the state
   */
  def parseCommand(command: String, player: Player, turnLoop: ActorRef) {
    command match {
      case "combat" =>
        turnLoop ! Combat
        turnLoop ! Fight
        turnLoop ! SecondMain
      case "end" =>
        performEndTurn
        turnLoop ! End
      case "exit" => turnLoop ! EndGame
      case "hand" => printHand(player)
      case "help" => printHelp
      case "play" => parseCardMenuOptions(player)
      case _ => println(s"You entered: ${command}, but I'm not sure how to handle that command.")
    }
  }

  def performEndTurn() {
    println(ansi"%blue{Ending turn.}")
    takingTurn = false
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
