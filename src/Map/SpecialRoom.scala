package Map

import Utils.Direction.Direction

import scala.collection.mutable.ArrayBuffer

class SpecialRoom(val diffulty: Int, val characterDir: Direction, val doorsDir: ArrayBuffer[Direction]) extends Room {
  override def createRoom(): Unit = {

  }
}
