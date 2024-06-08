package Map

import Characters.{Boss, Hero}
import Utils.{Direction, Vector2d}
import Utils.Direction.Direction
import ch.hevs.gdx2d.lib.GdxGraphics

import scala.collection.mutable.ArrayBuffer

class BossRoom(val diffulty: Int, val doorsDir: ArrayBuffer[Direction]) extends Room {
  val characterDir: Direction = Direction.NORTH
  doorsPositions = doorsDir

  private val BOSS_WIDTH: Float = squareWidth * 4
  private val boss: Boss = new Boss(ROOM_CENTER.add(new Vector2d(squareWidth/2, 0).sub(new Vector2d(BOSS_WIDTH/2, squareWidth/2))), BOSS_WIDTH)

  override def createRoom(): Unit = {

  }
  override def manageRoom(h: Hero){
    boss.manageBoss(h)
  }

  override def draw(g: GdxGraphics): Unit = {
    super.draw(g)
    boss.draw(g)
  }
}
