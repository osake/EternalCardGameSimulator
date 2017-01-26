package example

import org.scalatest._

class RunSpec extends FlatSpec with Matchers {
  "The Run object" should "say hello" in {
    Run.greeting should startWith("hello")
  }
}
