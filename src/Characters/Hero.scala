package Characters

import Utils.{Coordinate, Direction}
import Utils.Direction.Direction
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

import scala.collection.mutable.ArrayBuffer

class Hero(initialPos: Coordinate) extends DrawableObject{
  private val SPRITE_WIDTH: Int = 32
  private val SPRITE_HEIGHT: Int = 32

  private val NUM_FRAME_RUN: Int = 4
  private val FRAME_TIME: Double = 0.1

  private var textureY: Int = 0
  private var currentFrame: Int = 0
  private val runSs: Spritesheet = new Spritesheet("data/images/lumberjack_sheet32.png", SPRITE_WIDTH, SPRITE_HEIGHT)

  private var speed: Double = 1
  private var move: Boolean = false
  val position: Coordinate = initialPos

  private var dt: Double = 0

  def setSpeed(s: Double): Unit = {
    speed = s
  }

  def animate(elapsedTime: Double): Unit = {
    val frameTime = FRAME_TIME / speed

    if(isMoving){
      dt += elapsedTime
    }
    else{
      currentFrame = 0
      dt = 0
    }

    if(dt > frameTime){
      dt -= frameTime

      currentFrame = (currentFrame + 1) % NUM_FRAME_RUN

      if(currentFrame == 0){
        move = false
      }
    }
  }

  def turn(d: Direction): Unit = {
    d match{
      case Direction.SOUTH => textureY = 0
      case Direction.WEST => textureY = 1
      case Direction.EAST => textureY = 2
      case Direction.NORTH => textureY = 3
      case _ =>
    }
  }

  def go(directions: ArrayBuffer[Direction]): Unit = {
    move = true
    var length: Float = 1

    if(directions.length == 2){
      length = math.cos(math.Pi/4).toFloat
    }

    for(d: Direction <- directions){
      d match {
        case Direction.SOUTH => position.y -= length * speed.toFloat
        case Direction.WEST => position.x -= length * speed.toFloat
        case Direction.EAST => position.x += length * speed.toFloat
        case Direction.NORTH => position.y += length * speed.toFloat
        case _ =>
      }
    }
  }

  def isMoving: Boolean = {
    return move
  }

  def setMove(m: Boolean): Unit = {
    move = m
  }

  override def draw(g: GdxGraphics): Unit = {
    g.draw(runSs.sprites(textureY)(currentFrame), position.x, position.y)
  }
}
