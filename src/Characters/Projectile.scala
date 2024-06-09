package Characters

import Utils.Vector2d
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

import scala.util.Random

class Projectile(startPos: Vector2d, direction: Vector2d, distance: Float, hitboxHalfWidth: Float, var damage: Int,
                 var isFromHero: Boolean) extends DrawableObject {
  private var dt: Double = 0
  private val normalizedDir: Vector2d = direction.normalize()
  private var currentPos: Vector2d = startPos
  private var travelledDistance: Float = 0
  private val ARROW_SPRITE_WIDTH: Int = 16
  private val ARROW_SPRITE_HEIGHT: Int = ARROW_SPRITE_WIDTH
  private val arrowSs: Spritesheet = new Spritesheet("data/images/arrow.png", ARROW_SPRITE_WIDTH, ARROW_SPRITE_HEIGHT)
  private var rotation: Float = Math.toDegrees(Math.atan2(direction.y, direction.x)).toFloat // angle de rotation en degrés
  private val NUM_FRAME_RUN: Int = 4

  private var currentFrame: Int = 0

  var speed: Int = 10
  var isFinish: Boolean = false
  var hitbox: Hitbox = _

  addHitbox()

  def addHitbox(): Unit = {
    hitbox = new Hitbox(currentPos, hitboxHalfWidth*2, hitboxHalfWidth, rotation)
  }
  def setSpeed(s: Int): Unit = {
    speed = s
  }

  def animate(): Unit = {
    currentFrame = (currentFrame + 1) % NUM_FRAME_RUN
    currentPos = currentPos.add(normalizedDir.mult(speed))
    hitbox.updateCenter(currentPos)
    travelledDistance += speed
  }

  override def draw(g: GdxGraphics): Unit = {
    if(travelledDistance < distance){
      val scaleX: Float = 1 // facteur d'échelle en x
      val scaleY: Float = 1 // facteur d'échelle en y

      g.draw(arrowSs.sprites(0)(currentFrame), hitbox.center.x - hitboxHalfWidth, hitbox.center.y - hitboxHalfWidth,
        hitboxHalfWidth, hitboxHalfWidth, hitboxHalfWidth*2, hitboxHalfWidth*2, scaleX, scaleY, rotation)
    }
    else{
      isFinish = true
    }
  }
}
