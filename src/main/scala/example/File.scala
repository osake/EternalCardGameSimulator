package example

import scala.io.Source

class File(val filename: String) {
  def contents() : String = {
    return Source.fromFile(filename).getLines.mkString
  }
}
