package example

import scala.collection.mutable.ListBuffer

/**
  * Binary for executing the simulator.
  *
  * Reads the first argument as a given number of times to execute the simulator.
  */
object Run extends Greeting with App {
  var iterations = 10
  if (args.size > 0) {
    iterations = args(0).toInt
  }
  println(greeting)
  (1 to iterations) map { index =>
    val a = new Sim(new Deck(Prefab.gauntlet_thirty_deck()))
    val power_count = a.countType("p", a.hand)
    if (power_count <  2 || power_count > 5) {
      a.mulligan()
    }
    a.d.d foreach { c =>
      //println(c.generic_type + ", " + c.cost)
    }

    //println(a.d.d.size)
    println(a.mulligan_counter)
  }

  def card_count(t: String, list: ListBuffer[Card]) : Int = {
    return list.filter(c => c.generic_type == t).size
  }
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
