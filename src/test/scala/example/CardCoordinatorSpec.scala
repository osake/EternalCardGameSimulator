package example

import akka.testkit._
import akka.actor._
import org.scalatest._
import org.scalatest.concurrent._

class GameCoordinatorSpec extends TestKit(ActorSystem("game-system")) with FlatSpecLike with Matchers with ScalaFutures {
  "GameCoordinatorActor" should "do stuff for a new game" in {
    val game = TestFSMRef(new GameCoordinator)
    game.stateName shouldBe NotStarted
    game.stateData shouldBe Uninitialized
    //game ! Setup(playerArray)
    //game.stateData shouldBe //Some kind of state, maybe players at health, 7 cards in hands, etc.
  }
}
