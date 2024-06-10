package Characters.Projectiles

import Characters.Hitbox
import Utils.Vector2d
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics

class Arrow(startPos: Vector2d, direction: Vector2d, distance: Float, hitboxWidth: Float, damage: Int,
            isFromHero: Boolean) extends Projectile(damage, isFromHero) {
  private val normalizedDir: Vector2d = direction.normalize()
  private var currentPos: Vector2d = startPos
  private var travelledDistance: Float = 0
  private val ARROW_SPRITE_WIDTH: Int = 16
  private val ARROW_SPRITE_HEIGHT: Int = ARROW_SPRITE_WIDTH
  private val arrowSs: Spritesheet = new Spritesheet("data/images/arrow.png", ARROW_SPRITE_WIDTH, ARROW_SPRITE_HEIGHT)
  private val rotation: Float = Math.toDegrees(Math.atan2(direction.y, direction.x)).toFloat // angle de rotation en degrés
  private val NUM_FRAME_RUN: Int = 4

  private var currentFrame: Int = 0
  addHitbox()

  def addHitbox(): Unit = {
    hitbox = new Hitbox(currentPos, hitboxWidth, hitboxWidth/2, rotation)
  }

  def animate(): Unit = {
    currentFrame = (currentFrame + 1) % NUM_FRAME_RUN
    currentPos = currentPos.add(normalizedDir.mult(speed))
    hitbox.updateCenter(currentPos)
    travelledDistance += speed
  }

  override def draw(g: GdxGraphics): Unit = {
    if (travelledDistance < distance) {
      val scaleX: Float = 1 // facteur d'échelle en x
      val scaleY: Float = 1 // facteur d'échelle en y

      g.draw(arrowSs.sprites(0)(currentFrame), hitbox.center.x - hitboxWidth/2, hitbox.center.y - hitboxWidth/2,
        hitboxWidth/2, hitboxWidth/2, hitboxWidth, hitboxWidth, scaleX, scaleY, rotation)
    }
    else {
      isFinish = true
    }
  }
}
