package Characters

import Utils.Vector2d
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation

class Monster(initialPos: Vector2d, width: Float) extends DrawableObject{
  private val SPRITE_WIDTH: Int = 32
  private val SPRITE_HEIGHT: Int = SPRITE_WIDTH
  private val HITBOX_WIDTH: Float = width / 2
  private val HITBOX_HEIGHT: Float = 12 * width / SPRITE_WIDTH
  private val RELATIVE_CENTER_HITBOX: Vector2d = new Vector2d((width - HITBOX_WIDTH) / 2 + HITBOX_WIDTH / 2,
    (width - HITBOX_HEIGHT) / 2 + HITBOX_HEIGHT / 2)

  private val GROW_FACTOR = width / (SPRITE_WIDTH / 2)
  private val NUM_FRAME_RUN: Int = 4
  private val FRAME_TIME: Double = 0.1

  val DIFFICULTY: Int = 1

  //private var textureY: Int = 0
  private var currentFrame: Int = 0
  private val runSs: Spritesheet = new Spritesheet("data/images/slime2.png", SPRITE_WIDTH, SPRITE_HEIGHT)

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

  /*def goBack(CoordinateCenterHeroBase: Vector2d, CoordinateCenterBase: Vector2d, percentage: Float): Unit = {
    val relativeVector: Vector2d = Vector2d(-(CoordinateCenterHeroBase.x - hitbox.center.x),
      -(CoordinateCenterHeroBase.y - hitbox.center.y))
    val angle: Double = Math.atan2(relativeVector.y, relativeVector.x)
    val finalPos: Vector2d = new Vector2d((math.cos(angle) * GROW_FACTOR * 10).toFloat,
      (math.sin(angle) * GROW_FACTOR * 10).toFloat)

    position.x = Interpolation.linear.apply(CoordinateCenterBase.x, finalPos.x, percentage)
    position.y = Interpolation.linear.apply(CoordinateCenterBase.y, finalPos.y, percentage)
  }*/

  def go(CoordinateCenter: Vector2d): Unit = {
    val relativeVector: Vector2d = CoordinateCenter.sub(hitbox.center)
    val angle: Double = Math.atan2(relativeVector.y, relativeVector.x)

    position.x += (math.cos(angle) * speed * GROW_FACTOR).toFloat
    position.y += (math.sin(angle) * speed * GROW_FACTOR).toFloat

    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }

  def manageMonster(h: Hero): Unit = {
    animate(Gdx.graphics.getDeltaTime)

    if(hitbox.interect(h.hitbox)){
      h.setInvisibility(true)
    }

    go(h.hitbox.center)
  }

  override def draw(g: GdxGraphics): Unit = {
    g.draw(runSs.sprites(0)(currentFrame), position.x, position.y, width, width)
  }
}
