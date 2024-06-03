package Map

import Utils.Direction
import Utils.Direction.Direction

import scala.collection.mutable.ArrayBuffer

class BossRoom(val diffulty: Int, val doorsDir: ArrayBuffer[Direction]) extends Room {
  val characterDir: Direction = Direction.NORTH
  doorsPositions = doorsDir
  override def createRoom(): Unit = {

  }
}
