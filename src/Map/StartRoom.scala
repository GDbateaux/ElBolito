package Map

import Utils.Direction
import Utils.Direction.Direction

import scala.collection.mutable.ArrayBuffer

class StartRoom(val doorsDir: ArrayBuffer[Direction]) extends Room {
  var characterDir: Direction = Direction.NORTH
  doorsPositions = doorsDir
  override def createRoom(): Unit = {

  }
}
