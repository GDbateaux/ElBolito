package Map

import Characters.Hero
import Utils.Direction
import Utils.Direction.Direction
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx

import scala.collection.mutable.ArrayBuffer

class SpecialRoom(val doorsDir: ArrayBuffer[Direction]) extends Room {
  private val CHEST_SPRITE_WIDTH: Int = 16
  private val CHEST_SPRITE_HEIGHT: Int = CHEST_SPRITE_WIDTH
  private val SPRITE_NUMBER: Int = 8
  private val ANIMATION_TIME_PAUSE: Float = 1f
  private val FRAME_TIME: Float = 0.1f

  var dt: Float = 0
  val characterDir: Direction = Direction.NORTH
  val chestSs: Spritesheet = new Spritesheet("data/images/chest_spritesheet.png", CHEST_SPRITE_WIDTH, CHEST_SPRITE_HEIGHT)
  var srpiteX: Int = 0
  doorsPositions = doorsDir

  override def createRoom(): Unit = {

  }

  override def manageRoom(h: Hero): Unit = {
    dt += Gdx.graphics.getDeltaTime
    if(dt >= ANIMATION_TIME_PAUSE){
      if(dt >= FRAME_TIME + ANIMATION_TIME_PAUSE){
        dt -= FRAME_TIME
        srpiteX = (srpiteX + 1) % SPRITE_NUMBER
        if (srpiteX == 0) {
          dt = 0
        }
      }
    }
  }

  override def draw(g: GdxGraphics): Unit = {
    super.draw(g)
    g.draw(chestSs.sprites(0)(srpiteX), ROOM_CENTER.x, ROOM_CENTER.y, squareWidth, squareWidth)
  }
}
