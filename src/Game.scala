import Characters.{Hero, Monster, Projectile, ProjectileHandler}
import Map.Floor
import Utils.Direction.Direction
import Utils.{Direction, Screen, Vector2d}
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
 * ElBolito game in Scala
 *
 * @author Simon Masserey & Yolan Savioz
 * @version 1.0
 */
object Game {
  def main(args: Array[String]): Unit = {
    new Game(Screen.WIDTH, Screen.HEIGHT)
  }
}

class Game(windowWidth: Int, windowHeigth:Int) extends PortableApplication(windowWidth, windowHeigth) {
  private val ANIMATION_LENGTH_DAMAGE: Float = 1
  private val NUM_ROOM: Int = 10
  private val DRAW_HITBOX: Boolean = false

  private var h: Hero = _
  private var f: Floor = _
  private val keyStatus: mutable.HashMap[Int, Boolean] = new mutable.HashMap[Int, Boolean]()
  private val buttonStatus: mutable.HashMap[Int, Boolean] = new mutable.HashMap[Int, Boolean]()
  private val pointerPos: Vector2d = new Vector2d(0, 0)
  private var currentTime: Float = 0
  private var invincibilityTime: Float = 0

  private val KEY_UP = Input.Keys.W
  private val KEY_RIGHT = Input.Keys.D
  private val KEY_DOWN = Input.Keys.S
  private val KEY_LEFT = Input.Keys.A
  private val KEY_SHIFT = Input.Keys.SHIFT_LEFT
  private val BUTTON_LEFT = Input.Buttons.LEFT
  private val BUTTON_RIGHT = Input.Buttons.RIGHT

  override def onInit(): Unit = {
    setTitle("El Bolito")

    f = new Floor(NUM_ROOM, 0)
    h = new Hero(f.currentRoom.ROOM_CENTER, f.currentRoom.squareWidth)
    keyStatus(KEY_UP) = false
    keyStatus(KEY_RIGHT) = false
    keyStatus(KEY_DOWN) = false
    keyStatus(KEY_LEFT) = false
    keyStatus(KEY_SHIFT) = false
    buttonStatus(BUTTON_LEFT) = false
    buttonStatus(BUTTON_RIGHT) = false
  }

  /**
   * This method is called periodically by the engine
   *
   * @param g
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Clears the screen
    g.clear()

    f.draw(g)
    f.currentRoom.manageRoom(h)
    f.currentRoom.doorAnimate(Gdx.graphics.getDeltaTime)

    manageHero()
    h.draw(g)

    ProjectileHandler.handle(g, f.currentRoom, h)

    if(DRAW_HITBOX){
      h.hitbox.draw(g)
      h.attackHitbox.draw(g)
      for(m <- f.currentRoom.monsters){
        m.hitbox.draw(g)
      }
    }

    g.drawFPS()
  }

  private def manageHero(): Unit = {
    var goDir: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
    val dirNoGo: ArrayBuffer[Direction] = f.currentRoom.wallContact(h.hitbox)
    val dirSwitchRoom: Direction = f.currentRoom.doorContact(h.hitbox)

    h.setWeaponType(h.WEAPON_TYPE_BOW)
    h.setWeaponType(h.WEAPON_TYPE_SWORD)

    if (keyStatus(KEY_UP)) {
      h.turn(Direction.NORTH)
      goDir.append(Direction.NORTH)
    }
    if (keyStatus(KEY_DOWN)) {
      h.turn(Direction.SOUTH)
      goDir.append(Direction.SOUTH)
    }
    if (keyStatus(KEY_RIGHT)) {
      h.turn(Direction.EAST)
      goDir.append(Direction.EAST)
    }
    if (keyStatus(KEY_LEFT)) {
      h.turn(Direction.WEST)
      goDir.append(Direction.WEST)
    }
    if(buttonStatus(BUTTON_RIGHT)) {
      buttonStatus(BUTTON_RIGHT) = false
    }
    if (buttonStatus(BUTTON_LEFT)) {
      h.attack(pointerPos)
      buttonStatus(BUTTON_LEFT) = false
    }

    if (dirSwitchRoom != Direction.NULL && goDir.contains(dirSwitchRoom) && f.currentRoom.isClean) {
      if(dirSwitchRoom == Direction.SOUTH) {
        h.position.x = f.currentRoom.ROOM_NORTH.x
        h.position.y = f.currentRoom.ROOM_NORTH.y
      }
      else if (dirSwitchRoom == Direction.NORTH) {
        h.position.x = f.currentRoom.ROOM_SOUTH.x
        h.position.y = f.currentRoom.ROOM_SOUTH.y
      }
      else if (dirSwitchRoom == Direction.EAST) {
        h.position.x = f.currentRoom.ROOM_WEST.x
        h.position.y = f.currentRoom.ROOM_WEST.y
      }
      else if (dirSwitchRoom == Direction.WEST) {
        h.position.x = f.currentRoom.ROOM_EAST.x
        h.position.y = f.currentRoom.ROOM_EAST.y
      }
      f.changeRoom(dirSwitchRoom)
    }

    goDir = goDir.diff(dirNoGo)

    h.go(goDir)

    if(keyStatus(KEY_SHIFT)) {
      h.roll()
    }


    if(!keyStatus(KEY_UP) && !keyStatus(KEY_DOWN) &&
      !keyStatus(KEY_LEFT) && !keyStatus(KEY_RIGHT)){
      h.setMove(false)
    }

    if(invincibilityTime >= h.INVINCIBILITY_TIME){
      invincibilityTime = 0
      h.setInvisibility(false)
    }

    if(h.isInvincible){
      invincibilityTime += Gdx.graphics.getDeltaTime
    }

    if(h.hp <= 0) {
      h.setSpeed(0)
    }

    h.animate(Gdx.graphics.getDeltaTime)
  }

  override def onKeyUp(keyCode: Int): Unit = {
    super.onKeyUp(keyCode)
    keyStatus(keyCode) = false
  }

  override def onKeyDown(keyCode: Int): Unit = {
    super.onKeyDown(keyCode)
    keyStatus(keyCode) = true
  }

  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)
    pointerPos.x = x
    pointerPos.y = y
    buttonStatus(button) = true
  }
}