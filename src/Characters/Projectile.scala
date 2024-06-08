package Characters

import Utils.Vector2d
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

import scala.util.Random

class Projectile(startPos: Vector2d, direction: Vector2d, distance: Float, radius: Float, var damage: Int,
                 var isFromHero: Boolean) extends DrawableObject {
  private var dt: Double = 0
  private val normalizedDir: Vector2d = direction.normalize()
  private var currentPos: Vector2d = startPos
  private var travelledDistance: Float = 0
  private val ARROW_SPRITE_WIDTH: Int = 16;
  private val ARROW_SPRITE_HEIGHT: Int = ARROW_SPRITE_WIDTH;
  private var arrowSs: Spritesheet = new Spritesheet("data/images/arrow.png", ARROW_SPRITE_WIDTH, ARROW_SPRITE_HEIGHT)

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
      //g.drawCircle(currentPos.x, currentPos.y, radius)
      val rotation: Float = Math.toDegrees(Math.atan2(direction.y, direction.x)).toFloat // angle de rotation en degrés
      val originX: Float = 50 // l'origine de la rotation en x (au milieu de la texture si c'est 50)
      val originY: Float = 50 // l'origine de la rotation en y (au milieu de la texture si c'est 50)
      val scaleX: Float = 1 // facteur d'échelle en x
      val scaleY: Float = 1 // facteur d'échelle en y

      if(rotation <= 45 && rotation >= -45) {
        g.draw(arrowSs.sprites(0)(0), currentPos.x - 70 / 2, currentPos.y - 70/2, originX, originY, 70, 70, scaleX, scaleY, rotation);
      }
      else if(rotation > 45 && rotation <= 135) {
        g.draw(arrowSs.sprites(0)(0), currentPos.x - 100, currentPos.y - 70 /2, originX, originY, 70, 70, scaleX, scaleY, rotation);
      }
      else if(rotation > 135 && rotation < 225 || rotation >= -180 && rotation <= -135) {
        g.draw(arrowSs.sprites(0)(0), currentPos.x - 70 / 2, currentPos.y - 80, originX, originY, 70, 70, scaleX, scaleY, rotation);
      }
      else if(rotation > -135 && rotation < -45) {
        g.draw(arrowSs.sprites(0)(0), currentPos.x - 80, currentPos.y - 70 /2, originX, originY, 70, 70, scaleX, scaleY, rotation);
      }

      //int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY
    }
    else{
      isFinish = true
    }
  }
}
