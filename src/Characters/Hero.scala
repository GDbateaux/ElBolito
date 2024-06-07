package Characters

import Utils.{Direction, Position, Screen, Vector2d}
import Utils.Direction.Direction
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.g2d.TextureRegion

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Hero(initialPos: Vector2d, width: Float) extends DrawableObject{
  private val HERO_SPRITE_WIDTH: Int = 16
  private val HERO_SPRITE_HEIGHT: Int = HERO_SPRITE_WIDTH

  private val ATTACK_SPRITE_WIDTH: Int = 48
  private val ATTACK_SPRITE_HEIGHT: Int = ATTACK_SPRITE_WIDTH
  private val ATTACK_FRAME_NUMBER: Int = 4

  private val ROLL_SPRITE_WIDTH: Int = 16
  private val ROLL_SPRITE_HEIGHT: Int = ROLL_SPRITE_WIDTH
  private val ROLL_FRAME_NUMBER: Int = 9
  private val ROLL_COOLDOWN: Double = 6.0

  private val HEART_SPRITE_WIDTH: Int = 17
  private val HEART_SPRITE_HEIGHT: Int = HEART_SPRITE_WIDTH

  private val HITBOX_WIDTH: Float = 6 * width / HERO_SPRITE_WIDTH
  private val HITBOX_HEIGHT: Float = width / 3

  private val RELATIVE_CENTER_HITBOX: Vector2d = new Vector2d((width-HITBOX_WIDTH)/2 + HITBOX_WIDTH/2, HITBOX_HEIGHT/2)

  var INVINCIBILITY_TIME: Double = 1

  private val GROW_FACTOR = width / HERO_SPRITE_WIDTH
  private val NUM_FRAME_RUN: Int = 6
  private val FRAME_TIME: Double = 0.1
  private val MAX_HEALTH: Int = 6

  private var textureY: Int = 0
  private var currentRunFrame: Int = 0
  private var currentAttackFrame: Int = 0
  private var currentRollFrame: Int = 0
  private var currentTime: Double = 0.0
  private val runSs: Spritesheet = new Spritesheet("data/images/hero_run.png", HERO_SPRITE_WIDTH, HERO_SPRITE_HEIGHT)
  private val swordAttackSs: Spritesheet = new Spritesheet("data/images/hero_sword_attack.png", ATTACK_SPRITE_WIDTH,
    ATTACK_SPRITE_HEIGHT) // Pourquoi * 3 alors que c'est du 192x192 ?????
  private val rollSs: Spritesheet = new Spritesheet("data/images/hero_roll.png", ROLL_SPRITE_WIDTH, ROLL_SPRITE_HEIGHT)
  private var curentDirections: ArrayBuffer[Direction] = new ArrayBuffer[Direction]().addOne(Direction.SOUTH)
  private val heartSs: Spritesheet = new Spritesheet("data/images/heart.png", HEART_SPRITE_WIDTH, HEART_SPRITE_HEIGHT)

  private var speed: Double = 1
  private val rollSpeed: Double = 4
  private var lastRollTime: Double = 0.0
  private var move: Boolean = false

  private var attackFrameRemain: Int = -1
  private var rollFrameRemain: Int = -1

  val position: Vector2d = initialPos
  val hitbox: Hitbox = new Hitbox(position.add(RELATIVE_CENTER_HITBOX), HITBOX_WIDTH, HITBOX_HEIGHT)
  var isAttaking: Boolean = false
  var attackHitbox: Hitbox = new Hitbox(new Vector2d(0,0),0,0)
  var isInvincible: Boolean = false
  var invincibleTransparence: Boolean = false;
  var hp = MAX_HEALTH
  var projectileFactor: Float = 8

  private val projectileDistance: Float = width * projectileFactor
  private var dt: Double = 0
  private var dtInvincible: Double = 0;

  def setSpeed(s: Double): Unit = {
    speed = s
  }

  def setInvisibility(i: Boolean): Unit = {
    isInvincible = i
  }

  def animate(elapsedTime: Double): Unit = {
    var frameTime = FRAME_TIME / speed

    if(attackFrameRemain >= 0) {
      dt += elapsedTime
      if (dt > frameTime) {
        dt -= frameTime
        currentAttackFrame = (ATTACK_FRAME_NUMBER - 1) - attackFrameRemain

        if(currentAttackFrame == 1 || currentAttackFrame == 2) {
          //Ajouter la hitbox de dégats pendant 2 frame
          isAttaking = true
          if(textureY == 0){
            attackHitbox = new Hitbox(position.add(RELATIVE_CENTER_HITBOX.sub(new Vector2d(0, width).sub(
              new Vector2d(0, HITBOX_HEIGHT)))), width*2, width)
          }
          else if(textureY == 1){
            attackHitbox = new Hitbox(position.add(RELATIVE_CENTER_HITBOX.sub(new Vector2d(width, 0).sub(
              new Vector2d(HITBOX_WIDTH, 0)))), width, width*2)
          }
          else if(textureY == 2){
            attackHitbox = new Hitbox(position.add(RELATIVE_CENTER_HITBOX.add(new Vector2d(width, 0).sub(
              new Vector2d(HITBOX_WIDTH, 0)))),width,width*2)
          }
          else if (textureY == 3) {
            attackHitbox = new Hitbox(position.add(RELATIVE_CENTER_HITBOX.add(new Vector2d(0, width).sub(
              new Vector2d(0, HITBOX_HEIGHT)))), width*2, width)
          }

        }
        else{
          isAttaking = false
          attackHitbox = new Hitbox(new Vector2d(0,0),0,0)
        }
        attackFrameRemain -= 1
      }
    }
    else if(rollFrameRemain >= 0) {
      var frameTime = FRAME_TIME / rollSpeed

      dt += elapsedTime

      if (dt > frameTime) {
        dt -= frameTime

        if(rollFrameRemain == 0 || rollFrameRemain == 1 || rollFrameRemain == ROLL_FRAME_NUMBER) {
          currentRollFrame = (5 - 1) - rollFrameRemain
        }
        else if(rollFrameRemain == ROLL_FRAME_NUMBER - 1) {
          //First frame
          isInvincible = true
          currentRollFrame = 0
          lastRollTime = System.currentTimeMillis() / 1000.0
        }
        else {
          if(rollFrameRemain % 2 == 0) {
            currentRollFrame = 2
          } else {
            currentRollFrame = 3
          }
        }

        //currentRollFrame = (ROLL_FRAME_NUMBER - 1) - rollFrameRemain

        var length: Float = 1

        if (curentDirections.length == 2) {
          length = math.cos(math.Pi / 4).toFloat
        }

        for (d: Direction <- curentDirections) {
          d match {
            case Direction.SOUTH => position.y -= length * GROW_FACTOR * rollSpeed.toFloat
            case Direction.WEST => position.x -= length * GROW_FACTOR * rollSpeed.toFloat
            case Direction.EAST => position.x += length * GROW_FACTOR * rollSpeed.toFloat
            case Direction.NORTH => position.y += length * GROW_FACTOR * rollSpeed.toFloat
            case _ =>
          }
        }

        hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))

        if(rollFrameRemain == 0) {
          //Last frame
          isInvincible = false
        }

        rollFrameRemain -= 1
      }
    }
    else {
      dtInvincible += elapsedTime
      if (isMoving) {
        dt += elapsedTime
      }
      else {
        currentRunFrame = 0
        dt = 0
      }

      if (dtInvincible > frameTime) {
        dtInvincible -= frameTime
        if(isInvincible) {
          invincibleTransparence = !invincibleTransparence;
        }
        else {
          invincibleTransparence = false;
        }
      }

      if (dt > frameTime) {
        dt -= frameTime

        currentRunFrame = (currentRunFrame + 1) % NUM_FRAME_RUN

        if (currentRunFrame == 0) {
          move = false
        }
      }
    }
  }

  def turn(d: Direction): Unit = {
    if(attackFrameRemain < 0  && rollFrameRemain < 0) {
      d match {
        case Direction.SOUTH => textureY = 0
        case Direction.WEST => textureY = 1
        case Direction.EAST => textureY = 2
        case Direction.NORTH => textureY = 3
        case _ =>
      }
    }
  }

  def go(directions: ArrayBuffer[Direction]): Unit = {
    if(attackFrameRemain < 0 && rollFrameRemain < 0) {
      if(directions.nonEmpty) {
        curentDirections = directions.clone()
      }

      move = true

      var length: Float = 1

      if (directions.length == 2) {
        length = math.cos(math.Pi / 4).toFloat
      }

      for (d: Direction <- directions) {
        d match {
          case Direction.SOUTH => position.y -= length * GROW_FACTOR * speed.toFloat
          case Direction.WEST => position.x -= length * GROW_FACTOR * speed.toFloat
          case Direction.EAST => position.x += length * GROW_FACTOR * speed.toFloat
          case Direction.NORTH => position.y += length * GROW_FACTOR * speed.toFloat
          case _ =>
        }
      }

      hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
    }
  }

  def attack(pointer: Vector2d): Unit = {
    val p: Projectile = new Projectile(position, pointer.sub(position), projectileDistance, width/6, 1, true)
    ProjectileHandler.projectiles.append(p)
    if(attackFrameRemain < 0 && rollFrameRemain < 0) {
      val verticalDif = position.y - pointer.y
      val horizontalDif = position.x - pointer.x

      if (math.abs(verticalDif) > math.abs(horizontalDif)) {
        if (verticalDif > 0) turn(Direction.SOUTH) else turn(Direction.NORTH)
      } else {
        if (horizontalDif > 0) turn(Direction.WEST) else turn(Direction.EAST)
      }

      attackFrameRemain = ATTACK_FRAME_NUMBER - 1 //Start at 0
    }
  }

  def roll(): Unit = {
    var currentTime: Double = System.currentTimeMillis() / 1000.0;

    if(attackFrameRemain < 0 && rollFrameRemain < 0 && currentTime > lastRollTime + ROLL_COOLDOWN) {

      rollFrameRemain = ROLL_FRAME_NUMBER - 1; //Start at 0
    }
  }

  def isMoving: Boolean = {
    return move
  }

  def setMove(m: Boolean): Unit = {
    move = m
  }

  def drawHearts(g: GdxGraphics): Unit = {
    val space: Float = Screen.HEIGHT / 100
    val heartWidth: Float = width/2
    val posY: Float = Screen.HEIGHT - heartWidth - space
    var currentRegion: TextureRegion = heartSs.sprites(0)(0)

    for(i: Int <- 1 to MAX_HEALTH/2){
      if(hp-i*2 >= 0){
        currentRegion = heartSs.sprites(0)(0)
      }
      else if(hp-i*2 == -1){
        currentRegion = heartSs.sprites(0)(1)
      }
      else{
        currentRegion = heartSs.sprites(0)(2)
      }
      g.draw(currentRegion, (i-1)*heartWidth+space, posY, heartWidth, heartWidth)
    }
  }

  override def draw(g: GdxGraphics): Unit = {
    drawHearts(g)
    if (attackFrameRemain >= 0) {
      g.draw(swordAttackSs.sprites(textureY)(currentAttackFrame), position.x - width, position.y - width, width * 3 , width * 3) // Au bol (* 3 compréhensible car au lieu d'avoir une image 16x16 on a du 48x48)
    }
    else if(rollFrameRemain >= 0) {
      g.draw(rollSs.sprites(textureY)(currentRollFrame), position.x, position.y, width, width)
    }
    else if(!invincibleTransparence){
      g.draw(runSs.sprites(textureY)(currentRunFrame), position.x, position.y, width, width)
    }
  }
}