package Map

import Utils.Direction
import Utils.Direction.Direction

import scala.collection.mutable.ArrayBuffer

class SpecialRoom(val doorsDir: ArrayBuffer[Direction]) extends Room {
  val characterDir: Direction = Direction.NORTH
  doorsPositions = doorsDir
  override def createRoom(): Unit = {

  }
}
