package example

/**
  * Turn class for main game loop.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
abstract class Turn() extends GameState {
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

case class AITurn(simulator: Sim, playerOne: Player, playerTwo: Player) extends Turn {
  val a = simulator
  var playablePower: Option[Card] = None
  var playableUnit: Option[Card] = None

  def run() {
    // Now we're ready to play.
    while (!isGameOver) {
      // Begin turn
      println(a.whoseTurn().name + "'s turn.")
        // Before we draw, let's remove summoning sickness from your board
        a.activePlayer.board foreach (c => c.summonSickness = false)
        a.activePlayer.currentPower = a.activePlayer.maxPower
        showHand(a.activePlayer)
        // Reset your board
        a.activePlayer.board foreach { c =>
          c.attacking = false
        }
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

      // Simple defender should block to prevent lethal, let's just do chump blocking for now
      a.activePlayer.board foreach { c =>
        if (c.attacking && c.attack >= a.defendingPlayer.health && !a.defendingPlayer.board.isEmpty) {
          var chumps = a.defendingPlayer.board.filter { d => d.blocking == false }
          var chump = chumps.head
            chump.blocking = true
            c.blocked == true
            if (c.attack >= chump.health) {
              println("OMGOMGOMG THE VOID!")
                chump.blocking = false
                a.defendingPlayer.board -= chump
                a.defendingPlayer.v += chump
            }
          if (chump.attack >= c.health) {
            c.blocked = false
              a.activePlayer.board -= c
              a.activePlayer.v += c
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
        if (isGameOver) {
          if (playerOne.health < 1) println(s"PlayerTwo wins! ${a.playerTwo.first}")
            if (playerTwo.health < 1) println(s"PlayerOne wins! ${a.playerOne.first}")
        }

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
  }
}

case class SolitaireTurn(simulator: Sim, playerOne: Player, playerTwo: Player) extends Turn {
  def run() {
  }
}
