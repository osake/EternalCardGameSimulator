package example

import akka.actor.{ Props, ActorSystem }
import akka.pattern.gracefulStop
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.io.StdIn.readLine

/**
  * Binary for executing the simulator in Solitaire mode.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
object Solitaire extends App with PlayerInput {
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
    a.start
    a.activePlayer = a.coinToss
    a.activePlayer.first = true

    println(a.activePlayer.name + " goes first!")

    // Both players determine mulligan, we'll go aggressive and simple for the AI
    playerOne.showHand
    print("Do you wish to mulligan? (y/n) ")
    val playerOneMulligan = readLine()
    if (playerOneMulligan == 'y') {
      playerOne.mulligan
      println(s"${playerOne.name} mulliganed.")
    } else println(s"${playerOne.name} is keeping their hand.")

    val p2PowerCount = playerTwo.countType("Power", playerTwo.hand)
    if (p2PowerCount <  2 || p2PowerCount > 5) {
      println(s"${playerTwo.name} mulliganed.")
      playerTwo.mulligan()
    }

    val system = ActorSystem()

    val game = system.actorOf(Props[GameCoordinator])
    val humanTurnLoop = system.actorOf(Props(classOf[SolitaireTurn], a, playerOne, playerTwo))
    val simData = new SimData(a, Array(playerOne, playerTwo), humanTurnLoop)

    game ! Setup(simData)
    game ! Begin

    var takingTurn: Boolean = _

    if (simulator.activePlayer.human) takingTurn = true

    while (takingTurn) {
      parseCommand(getCommand())
    }

    // More janky sleeping
    Thread.sleep(15000)

    try {
      val stoppedLoop: Future[Boolean] = gracefulStop(humanTurnLoop, 2 seconds)
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

  def parseCommand(command: String) {
    command match {
      case "combat" => println("Placeholder for combat")
      case "end" => performEndTurn
      case "exit" => sys.exit(0) // Just exit 0 for now.
      case "hand" => printHand
      case "help" => printHelp
      case "main2" => println("Placeholder for 2nd main phase")
      case "play" => parseCardMenuOptions
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

  def printHand() {
    simulator.activePlayer.showHand
  }

  def parseCardMenuOptions() {
    var picked = false
    if (simulator.activePlayer.hand.size == 0) picked = true // guard against empty hand
    while (!picked) {
      val card = getCommand("Enter the number in square brackets of the card you want to play. ")
      val number = cliToInt(card)
      number match {
        case Some(n) => {
          if (n >= 0 && n < simulator.activePlayer.hand.size) {
            picked = true
            simulator.activePlayer.play(simulator.activePlayer.hand(n))
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
// TODO(jfrench): I left off in the middle of moving the player command parsing to Solitaire engine, then having solitaire turn just do either an AI turn or a player turn based on player inputs from Solitaire.  It feels a bit weird to control the FSM from here, but I'm not sure how else to do it just yet.  You have to get player input to determine the next state, but players are really flexible in what they can do, at least for now.  Maybe at some point it will make sense to put this back in the GameCoordinator?  Rather than having two Turn subclasses, we can simply run the turn logic and just ask if it's a human and see what they want to do. From their inputs, they can control the state machine a little.

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
}

