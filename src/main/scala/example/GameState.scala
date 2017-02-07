package example

import scala.collection.mutable.ListBuffer

trait GameState {
  var isGameOver = false // TODO(jfrench): We can fix this later to be in simulator or something
  var turnCounter = 0
  var maxTurns = 30 // Just for brevity... what is the actual limit?
}
