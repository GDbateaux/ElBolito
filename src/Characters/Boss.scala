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

  private val GROW_FACTOR = (width / (SPRITE_WIDTH / 2))/2
  private val NUM_FRAME_RUN: Int = 4
  private val FRAME_TIME: Double = 0.1

  val MAX_HP: Int = 50
  var hp: Int = MAX_HP

  private var textureY: Int = 0
  private var currentFrame: Int = 0
  private val runSs: Spritesheet = new Spritesheet("data/images/yeti_run.png", SPRITE_WIDTH, SPRITE_HEIGHT)
  private val chargeSs: Spritesheet = new Spritesheet("data/images/yeti_charge.png", SPRITE_WIDTH, SPRITE_HEIGHT)

  private val runSpeed: Double = 0.6;
  private val CHARGE_CASTING_CD = 3;
  private var castingCharge = -1;
  private val chargeSpeed: Double = 3
  private var speed: Double = runSpeed
  private val SPECTIAL_COOLDOWN: Int = 6;
  private var lastSpecialTime: Double = System.currentTimeMillis() / 1000.0;
  private var posToGo: Vector2d = new Vector2d(0, 0)
  private var isCharging: Boolean = false;
  val position: Vector2d = initialPos
  val hitbox: Hitbox = new Hitbox(position.add(RELATIVE_CENTER_HITBOX), HITBOX_WIDTH, HITBOX_HEIGHT)

  private var firstTimeManage: Boolean = true;

  private var dt: Double = 0

  def isDead: Boolean = {
    return hp <= 0
  }

  def setSpeed(s: Double): Unit = {
    speed = s
  }

  def animate(elapsedTime: Double): Unit = {
    val frameTime = FRAME_TIME / speed
    dt += elapsedTime

    if (dt > frameTime) {
      if(invincibleFrameRemain > 0) {
        invincibleTransparence = !invincibleTransparence
        invincibleFrameRemain -= 1
      }
      else {
        invincibleTransparence = false
      }

      dt -= frameTime

      if(castingCharge > 0)
      {
        currentFrame = (currentFrame + 1) % NUM_FRAME_RUN
        castingCharge -= 1;
        println(castingCharge)
      }
      else {
        currentFrame = (currentFrame + 1) % NUM_FRAME_RUN
      }
    }
    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }

  def go(CoordinateCenter: Vector2d): Unit = {
    val relativeVector: Vector2d = CoordinateCenter.sub(hitbox.center)
    val normalizedVector = relativeVector.normalize()
    val vectorToGo: Vector2d = new Vector2d(
      (normalizedVector.x * speed * GROW_FACTOR).toFloat,
      (normalizedVector.y * speed * GROW_FACTOR).toFloat
    )

    if(relativeVector.length() != 0){
      if (relativeVector.length() <= vectorToGo.length()) {
        position.x += relativeVector.x
        position.y += relativeVector.y
      } else {
        position.x += vectorToGo.x
        position.y += vectorToGo.y
      }
    }

    if(relativeVector.x <= 0){
      textureY = 1
    }
    else{
      textureY = 0
    }

    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }

  def manageBoss(h: Hero): Unit = {
    animate(Gdx.graphics.getDeltaTime)

    val currentTime = System.currentTimeMillis() / 1000.0;

    if(firstTimeManage) {
      lastSpecialTime = currentTime;
      firstTimeManage = false;
    }

    if (hitbox.intersect(h.hitbox) && !h.isInvincible) {
      h.hp -= 1
      h.setInvisibility(true)
    }

    if(invincibleFrameRemain <= 0 && hitbox.intersect(h.attackHitbox)) {
      hp -= 1
      invincibleFrameRemain = INVINCIBLE_FRAME
    }

    if(currentTime > lastSpecialTime + SPECTIAL_COOLDOWN) {
      posToGo.x = hitbox.center.x;
      posToGo.y = hitbox.center.y;
      castingCharge = CHARGE_CASTING_CD;
      isCharging = true;
      lastSpecialTime = currentTime;
    }
    else if(castingCharge == 0) {
      speed = chargeSpeed;
      posToGo.x = h.hitbox.center.x;
      posToGo.y = h.hitbox.center.y;
      castingCharge -= 1;
    }

    if(!isCharging) {
      if(invincibleFrameRemain <= 0 ) {
        speed = runSpeed;
        posToGo.x = h.hitbox.center.x;
        posToGo.y = h.hitbox.center.y;
      }
      else {
        posToGo.x = hitbox.center.x;
        posToGo.y = hitbox.center.y;
      }
    } else if(castingCharge <= 0 && math.abs(posToGo.x - hitbox.center.x) < 0.1 && math.abs(posToGo.y - hitbox.center.y) < 0.1) {
      isCharging = false;
    }

    go(posToGo)
  }

  def draw(g: GdxGraphics): Unit = {
    if(!invincibleTransparence) {
      if(!isCharging) {
        g.draw(runSs.sprites(textureY)(currentFrame), position.x, position.y, width, width)
      }
      else {
        g.draw(chargeSs.sprites(textureY)(currentFrame), position.x, position.y, width, width)
      }

    }
  }
}
