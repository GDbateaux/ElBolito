package Characters

import Utils.Vector2d
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

import scala.util.Random

class Projectile(startPos: Vector2d, direction: Vector2d, distance: Float, radius: Float, var damage: Int,
                 var isFromHero: Boolean) extends DrawableObject {
  private var dt: Double = 0
  private val normalizedDir: Vector2d = direction.normalize()
  private var currentPos: Vector2d = startPos
  private var travelledDistance: Float = 0

  var speed: Int = 10
  var isFinish: Boolean = false
  var hitbox: Hitbox = _

  addHitbox()

  def addHitbox(): Unit = {
    hitbox = new Hitbox(currentPos, radius*2, radius*2)
  }
  def setSpeed(s: Int): Unit = {
    speed = s
  }

  def animate(): Unit = {
    currentPos = currentPos.add(normalizedDir.mult(speed))
    hitbox.updateCenter(currentPos)
    travelledDistance += speed
  }
  override def draw(g: GdxGraphics): Unit = {
    if(travelledDistance < distance){
      g.drawCircle(currentPos.x, currentPos.y, radius)
    }
    else{
      isFinish = true
    }
  }
}
