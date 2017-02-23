package example

import akka.testkit._
import akka.actor._
import org.scalatest._
import org.scalatest.concurrent._
import example.model.Deck
import example.model.Player
import example.model.Prefab

class GameCoordinatorSpec extends TestKit(ActorSystem("game-system")) with FlatSpecLike with Matchers with ScalaFutures {
  "GameCoordinatorActor" should "do stuff for a new game" in {
    val game = TestFSMRef(new GameCoordinator)
    game.stateName shouldBe NotStarted
    game.stateData shouldBe UninitializedGame
    //game ! Setup(playerArray)
    //game.stateData shouldBe //Some kind of state, maybe players at health, 7 cards in hands, etc.
    val p1 = new Player("Foo", deck = new Deck(Prefab.minionsOfShadowAI()))
    val p2 = new Player("Bar", deck = new Deck(Prefab.minionsOfShadowAI()))
    val sim = new Sim(p1, p2)
    sim.activePlayer = p1
    val gameLoop = TestFSMRef(new AITurn(sim, p1, p2))
    game ! Setup(SimData(sim, Array(p1, p2), gameLoop))
    //game ! Begin
  }
}
