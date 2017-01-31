package example

import com.google.gson.Gson
import com.typesafe.scalalogging.LazyLogging
import scala.collection.mutable.ListBuffer
import scala.util.Random

// TODO(jfrench): Probably want a configuration file which defines deck size, etc.
// The tests would want to respec the size so I don't need 75 card decks in all the tests :)
// More thoughts: How about an FSM for handing turns?
/**
  * Sim is the heart of the simulator.  It's effectively the methods needed to play a game of cards.
  * It utilizes decks and cards, then extends methods such as mulligans, playing, and restarting.
  * A binary which utilizes the simulator will just utilize these common terms to navigate the sim
  * so that it can produce output based on given inputs.
  */
class Sim(val playerOne: Player, val playerTwo: Player) extends LazyLogging {

  var activePlayer: Player = _

  def start() {
    // Setup the simulator with a shuffled deck and a hand of cards for each player.
    playerOne.restart
    playerOne.sortPower

    playerTwo.restart
    playerTwo.sortPower
  }

  // Brainstorming out some methods for the simulator
  def table() {
  }

  // Who goes first?
  def coinToss() : Player = {
    if (Random.nextInt() % 2 == 0) playerOne else playerTwo
  }

  def nextPlayer() {
    // Seems like Scala should have an idiomatic way of doing this...
    if (activePlayer == playerOne)
      activePlayer = playerTwo
    else
      activePlayer = playerOne
  }

  def defendingPlayer() : Player = {
    if (activePlayer == playerOne) playerTwo else playerOne
  }

  /**
   * Utility for certain cards that can look at opponent's hand.
   */
  def peekAtOpponentHand() {
  }

  /**
   * A turn consists of phases:
   *
   * Simplified:
   * - Start turn
   * - Draw
   * - 1st main
   * - Combat
   * - 2nd main
   * - End (discard, cleanup, etc.)
   */
  def turn() {
  }

  def whoseTurn() : Player = {
    return activePlayer
  }

  def gameOver() {
  }

  def outputGameState() {
    printChar("*")
    println(s"Cards in deck: ${playerTwo.deck.size}, Power: ${playerTwo.power}/${playerTwo.maxPower}, Player health: ${playerTwo.health}")
    print("Cards in hand: ")
    playerTwo.hand foreach (c => print(s"${c.name}(${c.cost}), "))
    print("\n")
    print("Void: ")
    playerTwo.v foreach (c => print(s"${c.name}(${c.cost}), "))
    print("\n")
    print("Board: ")
    playerTwo.board foreach (c => print(s"${c.name}(${c.attack}/${c.health}), "))
    print("\n")
    printChar("=")
    print("Board: ")
    playerOne.board foreach (c => print(s"${c.name}(${c.attack}/${c.health}), "))
    print("\n")
    print("Void: ")
    playerOne.v foreach (c => print(s"${c.name}(${c.cost}), "))
    print("\n")
    print("Cards in hand: ")
    playerOne.hand foreach (c => print(s"${c.name}(${c.cost}), "))
    print("\n")
    println(s"Cards in deck: ${playerOne.deck.size}, Power: ${playerOne.power}/${playerOne.maxPower}, Player health: ${playerOne.health}")
    printChar("*")
  }

  def printChar(character : String) {
    (1 to 20) foreach (_ => print(character))
    print("\n")
  }

  def setAttacking(c : Card) {
    c.attacking = true
  }

  def performAttack() {
    activePlayer.board foreach { c=>
      c.exhausted = true
      c.attacking = false
      defendingPlayer.health -= c.attack
    }
  }

  def checkGameOver() : Boolean = {
    if (playerOne.health < 1 || playerTwo.health < 1) true else false
  }
}

