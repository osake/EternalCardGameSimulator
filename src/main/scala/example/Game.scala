package example



object Game {
}

case class Game(id: String, title: String, description: String)

object GameInstance {
}

case class GameInstance(id: String, game: Game, players: Array[Player])

