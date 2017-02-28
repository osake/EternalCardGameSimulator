package example

import example.model.Player



object Game {
}

case class Game(id: String, title: String, description: String)

object GameInstance {
}

case class GameInstance(id: String, game: Game, players: Array[Player])

