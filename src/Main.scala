import Characters.{Hero, Monster}
import Utils.Direction.Direction
import Utils.{Coordinate, Direction}
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
 * Hello World demo in Scala
 *
 * @author Pierre-André Mudry (mui)
 * @version 1.0
 */
object Main {

  def main(args: Array[String]): Unit = {
    new Main
  }
}

class Main extends PortableApplication(1920, 1080) {
  private var h: Hero = _
  private var m: Monster = _
  private val keyStatus: mutable.HashMap[Int, Boolean] = new mutable.HashMap[Int, Boolean]()

  private val KEY_UP = Input.Keys.W
  private val KEY_RIGHT = Input.Keys.D
  private val KEY_DOWN = Input.Keys.S
  private val KEY_LEFT = Input.Keys.A

  override def onInit(): Unit = {
    setTitle("El Bolito")

    h = new Hero(Coordinate(100,100))
    m = new Monster(Coordinate(200,200))
    m.setSpeed(0.8)
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
    manageHero()

    h.animate(Gdx.graphics.getDeltaTime)
    h.draw(g)

    m.animate(Gdx.graphics.getDeltaTime)
    m.go(h.position)
    m.draw(g)

    // Draw everything
    g.drawFPS()
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
