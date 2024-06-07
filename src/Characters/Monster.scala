package Characters

import Map.Obstacle
import Utils.Direction.Direction
import Utils.{AStar, Direction, Position, Vector2d}
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation

import scala.collection.mutable.ArrayBuffer

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

  var dirCantGo: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
  var hp: Int = 1
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

  /*def go(CoordinateCenter: Vector2d): Unit = {
    val relativeVector: Vector2d = CoordinateCenter.sub(hitbox.center)
    val angle: Double = Math.atan2(relativeVector.y, relativeVector.x)

    if(!(math.cos(angle) > 0 && dirCantGo.contains(Direction.EAST) ||
      math.cos(angle) < 0 && dirCantGo.contains(Direction.WEST))){
      position.x += (math.cos(angle) * speed * GROW_FACTOR).toFloat
    }
    if (!(math.sin(angle) > 0 && dirCantGo.contains(Direction.NORTH) ||
      math.cos(angle) < 0 && dirCantGo.contains(Direction.SOUTH))) {
      position.y += (math.sin(angle) * speed * GROW_FACTOR).toFloat
    }
    dirCantGo.clear()
    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }*/

  def go(CoordinateCenter: Vector2d): Unit = {
    val relativeVector: Vector2d = CoordinateCenter.sub(hitbox.center)
    val angle: Double = Math.atan2(relativeVector.y, relativeVector.x)

    position.x += (math.cos(angle) * speed * GROW_FACTOR).toFloat
    position.y += (math.sin(angle) * speed * GROW_FACTOR).toFloat

    hitbox.updateCenter(position.add(RELATIVE_CENTER_HITBOX))
  }

  /*def go(hero: Hero, obstacles: ArrayBuffer[Obstacle]): Unit = {
  val path = findPath(hero, obstacles)
  if (path.nonEmpty) {
    val nextPosition = path.head
    position.x = nextPosition.x
    position.y = nextPosition.y
  }
}*/

  def posToVector2d(position: Position, gridVectors: Array[Array[Vector2d]]): Vector2d = {
    return gridVectors(position.y)(position.x)
  }

  def vector2dToPos(vector2d: Vector2d, gridVectors: Array[Array[Vector2d]]): Position = {
    var goodX: Int = 0;
    var goodY: Int = 0;
    var lessDiff: Double = math.abs(gridVectors(goodY)(goodX).x - vector2d.x) + math.abs(gridVectors(goodY)(goodX).y - vector2d.y);
    for (y: Int <- gridVectors.indices) {
      for (x: Int <- gridVectors(0).indices) {
        val actualDiff = math.abs(gridVectors(y)(x).x - vector2d.x) + math.abs(gridVectors(y)(x).y - vector2d.y);
        if(actualDiff < lessDiff) {
          lessDiff = actualDiff
          goodX = x;
          goodY = y
        }
      }
    }
    return Position(goodX, goodY)
  }

  def findPath(hero: Hero, grid: Array[Array[Int]], gridVectors: Array[Array[Vector2d]]): ArrayBuffer[Vector2d] = {
    val res: ArrayBuffer[Vector2d] = new ArrayBuffer[Vector2d]();
    val ROOM_HEIGHT: Int = 11
    val ROOM_WIDTH: Int = 19
    val ROOM_CHARACTER: Int = 1
    val ROOM_MONSTER: Int = 2
    val ROOM_OBSTACLE: Int = 3

    // Créez une grille représentant votre espace de jeu. Chaque cellule de la grille correspond à une position possible pour le monstre.
    //val grid: Array[Array[Int]] = Array.ofDim(ROOM_HEIGHT, ROOM_WIDTH);

    // Initialisez la grille. Mettez 0 pour les cellules libres et 1 pour les cellules contenant des obstacles.
    for(y <- 0 until ROOM_HEIGHT) {
      for(x <- 0 until ROOM_WIDTH) {
        if(grid(y)(x) == ROOM_CHARACTER) {
          grid(y)(x) == 0;
        }
        else if(grid(y)(x) == ROOM_MONSTER) {
          grid(y)(x) == 0;
        }
        else if(grid(y)(x) == ROOM_OBSTACLE){
          grid(y)(x) == 1;
        }
      }
    }

    // Créez une instance de l'algorithme A* avec votre grille.
    val aStar: AStar = new AStar(grid)

    val heroPosition: Position = vector2dToPos(hero.position, gridVectors);
    val monsterPosition: Position = vector2dToPos(position, gridVectors);

    // Utilisez l'algorithme A* pour trouver le chemin le plus court du monstre au héros.
    val path: ArrayBuffer[Position] = aStar.findPath(monsterPosition, heroPosition);
    println("START");
    for(p <- path) {
      println(p.x + " " + p.y);
      res.addOne(posToVector2d(p, gridVectors));
    }
    println("END");
    return res
  }


  def manageMonster(h: Hero, path: ArrayBuffer[Vector2d]): Unit = {
    animate(Gdx.graphics.getDeltaTime)

    if(hitbox.intersect(h.hitbox) && !h.isInvincible) {
      h.hp -= 1
      h.setInvisibility(true)
    }

    if(hitbox.intersect(h.attackHitbox)) {
      hp -= 1
    }

    if(path.nonEmpty) {
      go(path(0))
    }
  }

  override def draw(g: GdxGraphics): Unit = {
    g.draw(runSs.sprites(0)(currentFrame), position.x, position.y, width, width)
  }
}
