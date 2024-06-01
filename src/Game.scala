import Characters.{Hero, Monster}
import Map.{FightRoom, Floor}
import Utils.Direction.Direction
import Utils.{Vector2d, Direction, Screen}
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.immutable.Vector2
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
 * Hello World demo in Scala
 *
 * @author Pierre-André Mudry (mui)
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
  private val DRAW_HITBOX: Boolean = true

  private var h: Hero = _
  private var m: Monster = _
  private var f: Floor = _
  private val keyStatus: mutable.HashMap[Int, Boolean] = new mutable.HashMap[Int, Boolean]()
  private var currentTime: Float = 0
  private var invincibilityTime: Float = 0

  private val KEY_UP = Input.Keys.W
  private val KEY_RIGHT = Input.Keys.D
  private val KEY_DOWN = Input.Keys.S
  private val KEY_LEFT = Input.Keys.A

  override def onInit(): Unit = {
    setTitle("El Bolito")

    f = new Floor(NUM_ROOM)
    h = new Hero(new Vector2d(0, 0), f.currentRoom.squareWidth)
    m = new Monster(new Vector2d(200, 200), f.currentRoom.squareWidth)
    m.setSpeed(0.5)

    keyStatus(KEY_UP) = false
    keyStatus(KEY_RIGHT) = false
    keyStatus(KEY_DOWN) = false
    keyStatus(KEY_LEFT) = false
  }

  /**
   * This method is called periodically by the engine
   *
   * @param g
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Clears the screen
    g.clear()

    f.changeRoom(Direction.NORTH)
    f.draw(g)
    /*for (d <- f.currentRoom.roomObstacles){
      d.hitbox.draw(g)
    }*/

    manageHero()
    h.draw(g)

    manageMonster()
    m.draw(g)

    if(DRAW_HITBOX){
      h.hitbox.draw(g)
      m.hitbox.draw(g)
    }

    g.drawFPS()
  }

  private def manageMonster(): Unit = {
    m.animate(Gdx.graphics.getDeltaTime)

    if(m.hitbox.interect(h.hitbox)){
      h.setInvisibility(true)
    }

    m.go(h.hitbox.center)
  }

  private def manageHero(): Unit = {
    val goDir: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
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
    h.go(goDir)

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
}
