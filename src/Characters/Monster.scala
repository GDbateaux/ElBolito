package Characters

import Utils.Direction.Direction
import Utils.{Direction, Vector2d}
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

  def go(CoordinateCenter: Vector2d): Unit = {
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
  }

  /*def go(hero: Hero, obstacles: ArrayBuffer[Obstacle]): Unit = {
  val path = findPath(hero, obstacles)
  if (path.nonEmpty) {
    val nextPosition = path.head
    position.x = nextPosition.x
    position.y = nextPosition.y
  }
}*/


  def findPath(hero: Hero, obstacles: ArrayBuffer[Obstacle]): List[Vector2d] = {
    // Créez une grille représentant votre espace de jeu. Chaque cellule de la grille correspond à une position possible pour le monstre.
    val grid = Array.ofDimInt

    // Initialisez la grille. Mettez 0 pour les cellules libres et 1 pour les cellules contenant des obstacles.
    for (x <- 0 until gameWidth; y <- 0 until gameHeight) {
      grid(x)(y) = if (obstacles.exists(obstacle => obstacle.hitbox.contains(new Vector2d(x, y)))) 1 else 0
    }

    // Créez une instance de l'algorithme A* avec votre grille.
    val aStar = new AStar(grid)

    // Utilisez l'algorithme A* pour trouver le chemin le plus court du monstre au héros.
    val path = aStar.findPath(monster.position, hero.position)

    // Convertissez le chemin (qui est une liste de cellules) en une liste de positions.
    path.map(cell => new Vector2d(cell.x, cell.y))
  }


  def manageMonster(h: Hero): Unit = {
    animate(Gdx.graphics.getDeltaTime)

    if(hitbox.intersect(h.hitbox) && !h.isInvincible) {
      h.hp -= 1
      h.setInvisibility(true)
    }

    if(hitbox.intersect(h.attackHitbox)) {
      hp -= 1
    }

    go(h.hitbox.center)
  }

  override def draw(g: GdxGraphics): Unit = {
    g.draw(runSs.sprites(0)(currentFrame), position.x, position.y, width, width)
  }
}
