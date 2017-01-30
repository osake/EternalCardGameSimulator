package example

import com.google.gson.Gson
import com.typesafe.scalalogging.LazyLogging
import scala.collection.mutable.ListBuffer

// TODO(jfrench): Probably want a configuration file which defines deck size, etc.
// The tests would want to respec the size so I don't need 75 card decks in all the tests :)
/**
  * Sim is the heart of the simulator.  It's effectively the methods needed to play a game of cards.
  * It utilizes decks and cards, then extends methods such as mulligans, playing, and restarting.
  * A binary which utilizes the simulator will just utilize these common terms to navigate the sim
  * so that it can produce output based on given inputs.
  */
class Sim(val playerOne: Player, val playerTwo: Player) extends LazyLogging {

  // Setup the simulator with a shuffled deck and a hand of cards for each player.
  playerOne.deck.shuffle
  playerOne.draw(7)

  playerTwo.deck.shuffle
  playerTwo.draw(7)

}

