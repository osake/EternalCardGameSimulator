package example

import scala.collection.mutable.ListBuffer

/**
  * Binary for executing the simulator.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
object Run extends Greeting with App {
  var iterations = 10
  if (args.size > 0) {
    iterations = args(0).toInt
  }

  val playerOne = new Player("Player One", deck = new Deck(Prefab.minionsOfShadowAI()))
  // val playerTwo = new Player("Player Two", deck = new Deck(Prefab.gauntlet_thirty_deck()))
  val playerTwo = new Player("Player Two", deck = new Deck(Prefab.studentsOfTimeAI()))

  val f = new File("data/cards.json")
  //println(f.contents)

  println(greeting)
  (1 to iterations) map { index =>
    val a = new Sim(playerOne, playerTwo)
    a.start
    a.activePlayer = a.coinToss
    a.activePlayer.first = true

    println(a.activePlayer.name + " goes first!")

    // Both players determine mulligan, we'll go aggressive and simple
    val p1PowerCount = playerOne.countType("Power", playerOne.hand)
    if (p1PowerCount <  2 || p1PowerCount > 5) {
      playerOne.mulligan()
    }

    val p2PowerCount = playerTwo.countType("Power", playerTwo.hand)
    if (p2PowerCount <  2 || p2PowerCount > 5) {
      playerTwo.mulligan()
    }

    var isGameOver = false // TODO(jfrench): We can fix this later to be in simulator or something
    var maxTurns = 20 // Just for brevity... what is the actual limit?
    var turnCounter = 0
    var playablePower: Option[Card] = None
    var playableUnit: Option[Card] = None
    // Now we're ready to play.
    while (!isGameOver) {
      // Begin turn
      println(a.whoseTurn().name + "'s turn.")
      // Before we draw, let's remove summoning sickness from your board
      a.activePlayer.board foreach (c => c.summonSickness = false)
      a.activePlayer.currentPower = a.activePlayer.maxPower
      showHand(a.activePlayer)
      // If the turn counter is 0 and player is first, just in case we decide to extract turnCounter
      // then we skip the draw phase
      if (turnCounter == 0 && a.activePlayer.first) {
        println("You go first... no card for you!")
      } else {
        a.activePlayer.draw(1)
        showCard(a.activePlayer.hand.last)
      }

      // First Main phase
      // - play a power, play a unit
      playablePower = a.activePlayer.hand.find(_.generic_type == "Power")
      if (!playablePower.isEmpty) a.activePlayer.play(playablePower.get)

      // Combat Phase -- this sucks as it's written because there is interaction
      // Let's perform a dumb attack
      println(s"${a.activePlayer.name} is attacking ${a.defendingPlayer.name}")
      if (a.defendingPlayer.board.isEmpty) {
        a.activePlayer.board foreach { c =>
          a.setAttacking(c)
        }
      }
      // Now the combat cases get a bit more complicated, this approach may not be most suitable
      // Let's look and see if we can overrun the opponent
      // determine trade or win attacks, keep all other units back
      a.activePlayer.board foreach { c =>
        a.setAttacking(c)
        a.defendingPlayer.board.foreach { d =>
          if (d.attack > c.health) {
            a.unsetAttacking(c)
          }
          if (d.health > c.attack) {
            a.unsetAttacking(c)
          }
        }
      }

      // Is there additive defense to kill/block me
      var defenseAtk = 0
      var defenseHp = 0
      a.defendingPlayer.board.foreach { d =>
        defenseAtk += d.attack
        defenseHp += d.health
      }
      a.activePlayer.board foreach { c =>
        if (defenseAtk >= c.health) a.unsetAttacking(c)
        if (defenseHp >= c.attack) a.unsetAttacking(c)
      }

      a.performAttack
      isGameOver = a.checkGameOver

      // TODO(jfrench): This looks terrible, so I'll look at how to make it more clean.
      if (!isGameOver) {
        // Second main phase
        playableUnit = a.activePlayer.hand.find(_.generic_type == "Unit")
        if (!playableUnit.isEmpty) a.activePlayer.play(playableUnit.get)

        // Prep for ending turn
        println(a.activePlayer.name + " has " + a.activePlayer.hand.size + " cards in hand.")

        // Discard step
        if (a.activePlayer.hand.size > 9) {
          println("Automated discard to mimic max hand of 9 at end of turn")
          println("Discarding: " +  a.activePlayer.discard(a.activePlayer.hand.last).name)
        }

        a.nextPlayer()
        turnCounter += 1 // maybe this makes sense to track on each player

        // Cleanup checks to see if we should set game over.
        isGameOver = turnCounter > maxTurns
      }
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

  def showHand(p: Player) {
    print(s"${p.name} hand: ")
    p.hand foreach { card =>
      showCard(card)
    }
    print("\n")
  }

  def showCard(card: Card) {
    print(card.name + ", " + card.cost)
  }
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
