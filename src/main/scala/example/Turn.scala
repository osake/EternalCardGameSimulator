package example

import org.backuity.ansi.AnsiFormatter.FormattedHelper
import scala.io.StdIn.readLine

/**
  * Turn class for main game loop.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
abstract class Turn(simulator: Sim, playerOne: Player, playerTwo: Player) extends GameState {
  var playablePower: Option[Card] = None
  var playableUnit: Option[Card] = None

  def performAITurn() {
    beginTurnSetup
    for (drawnCard <- drawPhase) println(s"You drew ${drawnCard.name} (${drawnCard.cost})")

    // First Main phase
    // - play a power, play a unit
    playablePower = simulator.activePlayer.hand.find(_.generic_type == "Power")
    if (!playablePower.isEmpty) simulator.activePlayer.play(playablePower.get)

    // Combat Phase -- this sucks as it's written because there is interaction
    // Let's perform a dumb attack
    println(ansi"%red{${simulator.activePlayer.name} is attacking ${simulator.defendingPlayer.name}}")
    if (simulator.defendingPlayer.board.isEmpty) {
      simulator.activePlayer.board foreach { c =>
        simulator.setAttacking(c)
      }
    }

    // Now the combat cases get a bit more complicated, this approach may not be most suitable
    // Let's look and see if we can overrun the opponent
    // determine trade or win attacks, keep all other units back
    simulator.activePlayer.board foreach { c =>
      simulator.setAttacking(c)
      simulator.defendingPlayer.board.foreach { d =>
        if (d.attack > c.health) simulator.unsetAttacking(c)
        if (d.health > c.attack) simulator.unsetAttacking(c)
      }
    }

    // Simple defender should block to prevent lethal, let's just do chump blocking for now
    simulator.activePlayer.board foreach { c =>
      if (c.attacking && c.attack >= simulator.defendingPlayer.health && !simulator.defendingPlayer.board.isEmpty) {
        var chumps = simulator.defendingPlayer.board.filter { d => d.blocking == false }
        var chump = chumps.head
        chump.blocking = true
        c.blocked == true
        if (c.attack >= chump.health) {
          println("OMGOMGOMG THE VOID!")
          chump.blocking = false
          simulator.defendingPlayer.board -= chump
          simulator.defendingPlayer.v += chump
        }
        if (chump.attack >= c.health) {
          c.blocked = false
          simulator.activePlayer.board -= c
          simulator.activePlayer.v += c
        }
      }
    }

    // Is there additive defense to kill/block me
    var defenseAtk = 0
    var defenseHp = 0
    simulator.defendingPlayer.board.foreach { d =>
      defenseAtk += d.attack
      defenseHp += d.health
    }

    simulator.activePlayer.board foreach { c =>
      if (defenseAtk >= c.health) simulator.unsetAttacking(c)
      if (defenseHp >= c.attack) simulator.unsetAttacking(c)
    }

    simulator.performAttack
    isGameOver = simulator.checkGameOver
    if (isGameOver) {
      if (playerOne.health < 1) println(s"${playerTwo.name} wins! ${playerTwo.first}")
      if (playerTwo.health < 1) println(s"${playerOne.name} wins! ${playerOne.first}")
    }

    // TODO(jfrench): This looks terrible, so I'll look at how to make it more clean.
    if (!isGameOver) {
      // Second main phase
      playableUnit = simulator.activePlayer.hand.find(_.generic_type == "Unit")
      if (!playableUnit.isEmpty) simulator.activePlayer.play(playableUnit.get)

      // Prep for ending turn
      println(simulator.activePlayer.name + " has " + simulator.activePlayer.hand.size + " cards in hand.")

      // Discard step
      if (simulator.activePlayer.hand.size > 9) {
        println("Automated discard to mimic max hand of 9 at end of turn")
        println("Discarding: " +  simulator.activePlayer.discard(simulator.activePlayer.hand.last).name)
      }

      simulator.nextPlayer()
      turnCounter += 1 // maybe this makes sense to track on each player

      // Cleanup checks to see if we should set game over.
      isGameOver = turnCounter > maxTurns
    }
  }

  def beginTurnSetup() {
    // Begin turn
    println(simulator.activePlayer.name + "'s turn.")

    // Before we draw, let's remove summoning sickness from your board
    simulator.activePlayer.board foreach (c => c.summonSickness = false)
    simulator.activePlayer.currentPower = simulator.activePlayer.maxPower
    simulator.activePlayer.showHand

    // Reset your board
    simulator.activePlayer.board foreach { c =>
      c.attacking = false
    }
  }

  def drawPhase() : Option[Card] = {
    // If the turn counter is 0 and player is first, just in case we decide to extract turnCounter
    // then we skip the draw phase
    if (turnCounter == 0 && simulator.activePlayer.first) {
      println("You go first... no card for you!")
      return None
    } else {
      // TODO(jfrench): Would this be better to return the drawn card(s)?
      simulator.activePlayer.draw(1)
      return Some(simulator.activePlayer.hand.last)
    }
  }
}

case class AITurn(simulator: Sim, playerOne: Player, playerTwo: Player) extends Turn(simulator, playerOne, playerTwo) {
  def run() {
    // Now we're ready to play.
    while (!isGameOver) {
      performAITurn
    }
  }
}


case class SolitaireTurn(simulator: Sim, playerOne: Player, playerTwo: Player) extends Turn(simulator, playerOne, playerTwo) with PlayerInput {
  // TODO(jfrench): Fix this so that it takes player inputs, etc.
  def run() {
    // Now we're ready to play.
    while (!isGameOver) {
      if (simulator.activePlayer.human) {
        beginTurnSetup
        // TODO(french): Remove duplication
        for (drawnCard <- drawPhase) println(s"You drew ${drawnCard.name} (${drawnCard.cost})")

        println("Type 'help' to get list of commands.")
        parseCommand(getCommand())

        // bump turn
        readLine("Press enter.")
        simulator.nextPlayer()
        turnCounter += 1 // maybe this makes sense to track on each player

        // Cleanup checks to see if we should set game over.
        isGameOver = turnCounter > maxTurns
      } else {
        performAITurn
      }
      if (isGameOver) getAnyKey
    }
  }

  def parseCommand(command: String) {
    command match {
      case "help" => printHelp
      case "exit" => sys.exit(0) // Just exit 1 for now.
      case _ => println(s"You entered: ${command}, but I'm not sure how to handle that command.")
    }
  }

  def printHelp() {
    println("This is no help, it's a space station.")
    println("\nCurrent supported commands are: help and exit")
  }
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
}
