package Characters.Projectiles

import Characters.Hitbox
import Utils.Vector2d
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics

class Balls(startPos: Vector2d, direction: Vector2d, distance: Float, hitboxWidth: Float, damage: Int,
            isFromHero: Boolean) extends Projectile(damage, isFromHero) {
  private val normalizedDir: Vector2d = direction.normalize()
  private var currentPos: Vector2d = startPos
  private var travelledDistance: Float = 0
  private val BALL_SPRITE_WIDTH: Int = 16
  private val BALL_SPRITE_HEIGHT: Int = BALL_SPRITE_WIDTH
  private val ballRightSs: Spritesheet = new Spritesheet("data/images/yeti_ball_right.png", BALL_SPRITE_WIDTH, BALL_SPRITE_HEIGHT)
  private val ballLeftSs: Spritesheet = new Spritesheet("data/images/yeti_ball_left.png", BALL_SPRITE_WIDTH, BALL_SPRITE_HEIGHT)
  private val rotation: Float = Math.toDegrees(Math.atan2(direction.y, direction.x)).toFloat // angle de rotation en degrés
  private val NUM_FRAME_RUN: Int = 4

  private var currentFrame: Int = 0
  addHitbox()

  def addHitbox(): Unit = {
    hitbox = new Hitbox(currentPos, hitboxWidth, hitboxWidth/2, rotation)
  }

  def animate(): Unit = {
    currentPos = currentPos.add(normalizedDir.mult(speed))
    hitbox.updateCenter(currentPos)
    travelledDistance += speed
  }

  override def draw(g: GdxGraphics): Unit = {
    if (travelledDistance < distance) {
      val scaleX: Float = 1 // facteur d'échelle en x
      val scaleY: Float = 1 // facteur d'échelle en y

      if(rotation < 90 && rotation > -90) {
        g.draw(ballRightSs.sprites(0)(currentFrame), hitbox.center.x - hitboxWidth / 2, hitbox.center.y - hitboxWidth / 2,
          hitboxWidth / 2, hitboxWidth / 2, hitboxWidth, hitboxWidth, scaleX, scaleY, rotation)
      }
      else {
        g.draw(ballLeftSs.sprites(0)(currentFrame), hitbox.center.x - hitboxWidth / 2, hitbox.center.y - hitboxWidth / 2,
          hitboxWidth / 2, hitboxWidth / 2, hitboxWidth, hitboxWidth, scaleX, scaleY, rotation)
      }
      hitbox.draw(g);
    }
    else {
      isFinish = true
    }
  }
}

