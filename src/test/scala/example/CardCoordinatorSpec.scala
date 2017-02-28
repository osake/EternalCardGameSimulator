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
    // TODO(jfrench): Coordinator is Solitaire, and the AI runner is falling behind.
    //    As the two start to merge, we can safely return this test.  At the moment
    //    it's a little weird to try and do this without AI logic in the Coordinator.
    val p1 = new Player("Foo", deck = new Deck(Prefab.minionsOfShadowAI()))
    val p2 = new Player("Bar", deck = new Deck(Prefab.minionsOfShadowAI()))
    val sim = new Sim(p1, p2)
    sim.activePlayer = p1
    //val game = TestFSMRef(new GameCoordinator(TestActorRef(new Coordinator(sim, p1, p2, system)).underlyingActor))
    //game.stateName shouldBe NotStarted
    //game.stateData shouldBe UninitializedGame

    //val gameLoop = TestFSMRef(new AITurn(sim, p1, p2))
    //game ! Setup(SimData(sim, Array(p1, p2), gameLoop))
    //game ! Begin
  }
}
