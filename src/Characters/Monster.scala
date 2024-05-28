package Characters

import Utils.Coordinate
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

class Monster(initialPos: Coordinate) extends DrawableObject{
  private val SPRITE_WIDTH: Int = 32
  private val SPRITE_HEIGHT: Int = 32

  private val NUM_FRAME_RUN: Int = 4
  private val FRAME_TIME: Double = 0.1

  //private var textureY: Int = 0
  private var currentFrame: Int = 0
  private val runSs: Spritesheet = new Spritesheet("data/images/slime2.png", SPRITE_WIDTH, SPRITE_HEIGHT)

  private var speed: Double = 1
  val position: Coordinate = initialPos

  private var dt: Double = 0

  def setSpeed(s: Double): Unit = {
    speed = s
  }

  def animate(elapsedTime: Double): Unit = {
    val frameTime = FRAME_TIME / speed
    dt += elapsedTime

    if (dt > frameTime) {
      dt -= frameTime
      currentFrame = (currentFrame + 1) % NUM_FRAME_RUN
    }
  }

  def go(heroCoordinate: Coordinate): Unit = {
    val relativeVector: Coordinate = Coordinate(heroCoordinate.x - position.x, heroCoordinate.y - position.y)
    val angle: Double = Math.atan2(relativeVector.y, relativeVector.x)

    position.x += (math.cos(angle) * speed).toFloat
    position.y += (math.sin(angle) * speed).toFloat

  }

  /*def turn(d: Dire): Unit = {
    d match {
      case Direction.SOUTH => textureY = 0
      case Direction.WEST => textureY = 1
      case Direction.EAST => textureY = 2
      case Direction.NORTH => textureY = 3
      case _ =>
    }
  }*/

  /*def go(d: Direction): Unit = {
    move = true
    d match {
      case Direction.SOUTH => position.y -= 1
      case Direction.WEST => position.x -= 1
      case Direction.EAST => position.x += 1
      case Direction.NORTH => position.y += 1
      case _ =>
    }
  }*/

  override def draw(g: GdxGraphics): Unit = {
    g.draw(runSs.sprites(0)(currentFrame), position.x, position.y)
  }
}
