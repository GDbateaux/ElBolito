package Map

import Characters.{Hero, Hitbox}
import Map.Items.{Bow, Heart, Item}
import Utils.{Direction, Vector2d}
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

  private var dt: Float = 0
  private val chestSs: Spritesheet = new Spritesheet("data/images/chest_spritesheet.png", CHEST_SPRITE_WIDTH, CHEST_SPRITE_HEIGHT)
  private val chestOpenSs: Spritesheet = new Spritesheet("data/images/chest_open.png", CHEST_SPRITE_WIDTH, CHEST_SPRITE_HEIGHT)
  private val chestHitbox: Hitbox = new Hitbox(new Vector2d(ROOM_CENTER.x + squareWidth / 2, ROOM_CENTER.y + squareWidth / 2),
    squareWidth, squareWidth)
  private var srpiteX: Int = 0
  private var isChestOpen: Boolean = false

  private var text: String = ""
  val characterDir: Direction = Direction.NORTH
  doorsPositions = doorsDir

  override def createRoom(): Unit = {

  }

  override def manageRoom(h: Hero): Unit = {
    animate()

    if(h.hitbox.intersect(chestHitbox) && !isChestOpen){
      var item: Item = new Heart

      isChestOpen = true
      if(h.numberWeapons == 1){
        h.numberWeapons += 1
        item = new Bow
      }
      item.handle(h)
      text = item.text
    }
  }

  private def animate(): Unit = {
    dt += Gdx.graphics.getDeltaTime
    if (dt >= ANIMATION_TIME_PAUSE) {
      if (dt >= FRAME_TIME + ANIMATION_TIME_PAUSE) {
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
    if(isChestOpen){
      g.draw(chestOpenSs.sprites(0)(0), ROOM_CENTER.x, ROOM_CENTER.y, squareWidth, squareWidth)
    }
    else{
      g.draw(chestSs.sprites(0)(srpiteX), ROOM_CENTER.x, ROOM_CENTER.y, squareWidth, squareWidth)
    }

    if(text != ""){
      g.drawStringCentered(ROOM_CENTER.y + squareWidth * 1.5f, text)
    }
  }
}
