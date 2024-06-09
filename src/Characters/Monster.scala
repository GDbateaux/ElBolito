package Characters

import Map.Obstacle
import Utils.Direction.Direction
import Utils.{AStar, Direction, Position, Vector2d}
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation

import scala.collection.mutable.ArrayBuffer

class Monster(initialPos: Vector2d, width: Float) extends DrawableObject with Enemy {
  private val SPRITE_WIDTH: Int = 32
  private val SPRITE_HEIGHT: Int = SPRITE_WIDTH
  private val HITBOX_WIDTH: Float = width / 2
  private val HITBOX_HEIGHT: Float = 12 * width / SPRITE_WIDTH
  private val RELATIVE_CENTER_HITBOX: Vector2d = new Vector2d(width / 2, width/ 2)

  private val GROW_FACTOR = width / (SPRITE_WIDTH / 2)
  private val NUM_FRAME_RUN: Int = 4
  private val FRAME_TIME: Double = 0.1

  var hp: Int = 3
  val DIFFICULTY: Int = 1
  private var isInvincible: Boolean = false

  //private var textureY: Int = 0
  private var currentFrame: Int = 0
  private val runSs: Spritesheet = new Spritesheet("data/images/slime2.png", SPRITE_WIDTH, SPRITE_HEIGHT)

  private var speed: Double = 1
  private var posToGo: Vector2d = new Vector2d(0, 0)
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

    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }

  private def posToVector2d(position: Position, gridVectors: Array[Array[Vector2d]]): Vector2d = {
    return gridVectors(position.y)(position.x)
  }

  private def vector2dToPos(vector2d: Vector2d, gridVectors: Array[Array[Vector2d]], grid: Array[Array[Int]]): Position = {
    var goodX: Int = 0
    var goodY: Int = 0
    var lessDiff: Double = math.abs(gridVectors(goodY)(goodX).x - vector2d.x) + math.abs(gridVectors(goodY)(goodX).y - vector2d.y)

    for (y: Int <- gridVectors.indices) {
      for (x: Int <- gridVectors(0).indices) {
        val actualDiff = math.abs(gridVectors(y)(x).x - vector2d.x) + math.abs(gridVectors(y)(x).y - vector2d.y)
        if(actualDiff < lessDiff) {
          lessDiff = actualDiff
          goodX = x
          goodY = y
        }
      }
    }
    return Position(goodX, goodY)
  }

  private def findPath(hero: Hero, grid: Array[Array[Int]], gridVectors: Array[Array[Vector2d]]): ArrayBuffer[Vector2d] = {
    val res: ArrayBuffer[Vector2d] = new ArrayBuffer[Vector2d]()

    val aStar: AStar = new AStar(grid)

    val heroPosition: Position = vector2dToPos(hero.hitbox.center, gridVectors, grid)
    val monsterPosition: Position = vector2dToPos(hitbox.center, gridVectors, grid)

    // Utilisez l'algorithme A* pour trouver le chemin le plus court du monstre au héros.
    val path: ArrayBuffer[Position] = aStar.findPath(monsterPosition, heroPosition)

    for(p <- path) {
      res.addOne(posToVector2d(p, gridVectors))
    }

    return res
  }

  def manageMonster(h: Hero, grid: Array[Array[Int]], gridVectors: Array[Array[Vector2d]], squareWidth: Float): Unit = {
    animate(Gdx.graphics.getDeltaTime)

    if(hitbox.intersect(h.hitbox) && !h.isInvincible) {
      h.hp -= 1
      h.setInvisibility(true)
    }

    if(isInvincible && h.attackHitbox.center.y == 0 && h.attackHitbox.center.x == 0) {
      isInvincible = false;
      speed = 1
    }

    if(!isInvincible && hitbox.intersect(h.attackHitbox)) {
      hp -= 1
      isInvincible = true;
    }

    if(math.abs(posToGo.x - hitbox.center.x) < 0.1 && math.abs(posToGo.y - hitbox.center.y) < 0.1 || posToGo.x == 0 && posToGo.y == 0) {
      val path = findPath(h, grid, gridVectors)

      //Path(0) is the actual monster position
      if (path.length >= 2) {
        posToGo.x = path(1).x
        posToGo.y = path(1).y
        go(posToGo)
      }
      else {
        posToGo = h.hitbox.center
        go(posToGo)
      }
    }
    else {
      go(posToGo)
    }
  }

  override def draw(g: GdxGraphics): Unit = {
    g.draw(runSs.sprites(0)(currentFrame), position.x, position.y, width, width)
  }
}
