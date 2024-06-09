package Characters

import Utils.Direction.Direction
import Utils.{Direction, Vector2d}
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx

import scala.collection.mutable.ArrayBuffer

class Boss(initialPos: Vector2d, width: Float) extends Enemy {
  private val SPRITE_WIDTH: Int = 64
  private val SPRITE_HEIGHT: Int = SPRITE_WIDTH
  private val HITBOX_WIDTH: Float = width / 2
  private val HITBOX_HEIGHT: Float = 26 * width / SPRITE_WIDTH
  private val RELATIVE_CENTER_HITBOX: Vector2d = new Vector2d(width/2, width/5)

  private val GROW_FACTOR = width / (SPRITE_WIDTH / 2)
  private val NUM_FRAME_RUN: Int = 4
  private val FRAME_TIME: Double = 0.1

  val MAX_HP: Int = 50
  var hp: Int = MAX_HP

  private var textureY: Int = 0
  private var currentFrame: Int = 0
  private val runSs: Spritesheet = new Spritesheet("data/images/yeti_run.png", SPRITE_WIDTH, SPRITE_HEIGHT)

  private var isInvincible: Boolean = false
  private var speed: Double = 1
  val position: Vector2d = initialPos
  val hitbox: Hitbox = new Hitbox(position.add(RELATIVE_CENTER_HITBOX), HITBOX_WIDTH, HITBOX_HEIGHT)

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
    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }

  def go(CoordinateCenter: Vector2d): Unit = {
    val relativeVector: Vector2d = CoordinateCenter.sub(hitbox.center)
    val angle: Double = Math.atan2(relativeVector.y, relativeVector.x)

    position.x += (math.cos(angle) * speed * GROW_FACTOR).toFloat
    position.y += (math.sin(angle) * speed * GROW_FACTOR).toFloat

    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }

  def manageBoss(h: Hero): Unit = {
    animate(Gdx.graphics.getDeltaTime)

    if (hitbox.intersect(h.hitbox) && !h.isInvincible) {
      h.hp -= 1
      h.setInvisibility(true)
    }

    if (isInvincible && h.attackHitbox.center.y == 0 && h.attackHitbox.center.x == 0) {
      isInvincible = false;
    }

    if (!isInvincible && hitbox.intersect(h.attackHitbox)) {
      hp -= 1
      isInvincible = true;
    }

    //go(h.hitbox.center)
  }

  def draw(g: GdxGraphics): Unit = {
    if(!invincibleTransparence) {
      g.draw(runSs.sprites(textureY)(currentFrame), position.x, position.y, width, width)
    }
  }
}
