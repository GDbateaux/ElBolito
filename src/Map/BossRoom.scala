package Map

import Characters.{Boss, Hero}
import Utils.{Direction, Screen, Vector2d}
import Utils.Direction.Direction
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics
import com.badlogic.gdx.graphics.Color

import scala.collection.mutable.ArrayBuffer

class BossRoom(val diffulty: Int, val doorsDir: ArrayBuffer[Direction]) extends Room {
  val characterDir: Direction = Direction.NORTH

  private val BOSS_WIDTH: Float = squareWidth * 4
  private val boss: Boss = new Boss(ROOM_CENTER.add(new Vector2d(squareWidth/2, 0).sub(new Vector2d(BOSS_WIDTH/2, squareWidth/2))), BOSS_WIDTH)

  createRoom()

  override def createRoom(): Unit = {
    doorsPositions = doorsDir
    isClean = false
    curentDoorFrame = 0
    boss.setSpeed(0.6)
    monsters.append(boss)
  }

  override def manageRoom(h: Hero){
    if(boss.isDead){
      monsters.subtractOne(boss)
      isClean = true
    }
    else{
      boss.manageBoss(h)
    }
  }

  def drawHp(g: GdxGraphics): Unit = {
    val hpPosX: Float = Screen.WIDTH / 2
    val hpPosY: Float = ROOM_SOUTH.y - squareWidth
    val hpWidth: Float = Screen.WIDTH / 4
    val hpHeight: Float = squareWidth / 2
    val c: Color = new Color(Color.valueOf("7A0000"))
    val percentage: Float = boss.hp.toFloat / boss.MAX_HP

    if(percentage >= 0){
      g.setColor(c)
      g.drawFilledRectangle(hpPosX - (hpWidth - hpWidth * percentage) / 2, hpPosY, hpWidth * percentage, hpHeight, 0)
    }
  }

  override def draw(g: GdxGraphics): Unit = {
    super.draw(g)
    drawHp(g)
    if(!boss.isDead){
      boss.draw(g)
    }
  }
}
