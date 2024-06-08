package Map

import Characters.{Hitbox, Monster}
import Utils.{Direction, Screen, Vector2d}
import Utils.Direction.Direction
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, Texture}

import scala.collection.mutable.ArrayBuffer

//Type de room: 0: Chill, 1: Battle, 2: Boss, 3: BigFoot (pas sûr pour le 3)
trait Room extends DrawableObject {
  protected val ROOM_HEIGHT: Int = 11
  protected val ROOM_WIDTH: Int = 19

  protected val ROOM_CHARACTER: Int = 1
  protected val ROOM_MONSTER: Int = 2
  protected val ROOM_OBSTACLE: Int = 3

  var room: Array[Array[Int]] = Array.ofDim(ROOM_HEIGHT, ROOM_WIDTH)
  var roomVectors: Array[Array[Vector2d]] = Array.ofDim(ROOM_HEIGHT, ROOM_WIDTH)
  protected var doorsPositions: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
  var monsters: ArrayBuffer[Monster] = new ArrayBuffer[Monster]()

  private val nbrSquareX: Int = ROOM_WIDTH + 4 // 2 = les murs droites et gauches (+ over)
  private val nbrSquareY: Int = ROOM_HEIGHT + 4 // 2 = les murs en haut et en bas (+ over)
  private var spaceWidth: Double = 0
  private var spaceHeight: Double = 0
  private var firstDraw: Boolean = true
  private val HERO_SPRITE_WIDTH: Int = 32
  private val HERO_SPRITE_HEIGHT: Int = HERO_SPRITE_WIDTH

  private val DOOR_FRAME_NUMBER: Int = 15
  protected var curentDoorFrame: Int = DOOR_FRAME_NUMBER - 1

  private val doorTopSs: Spritesheet = new Spritesheet("data/images/doorTop.png", HERO_SPRITE_WIDTH, HERO_SPRITE_HEIGHT)
  private val doorRightSs: Spritesheet = new Spritesheet("data/images/doorRight.png", HERO_SPRITE_WIDTH, HERO_SPRITE_HEIGHT)
  private val doorBotSs: Spritesheet = new Spritesheet("data/images/doorBot.png", HERO_SPRITE_WIDTH, HERO_SPRITE_HEIGHT)
  private val doorLeftSs: Spritesheet = new Spritesheet("data/images/doorLeft.png", HERO_SPRITE_WIDTH, HERO_SPRITE_HEIGHT)

  private val FRAME_TIME: Double = 0.1
  private var speed: Double = 1
  private var dt: Double = 0

  var ROOM_EAST: Vector2d = _
  var ROOM_WEST: Vector2d = _
  var ROOM_NORTH: Vector2d = _
  var ROOM_SOUTH: Vector2d = _
  var ROOM_CENTER: Vector2d = _

  val roomDoors: ArrayBuffer[Door] = new ArrayBuffer[Door]()
  val roomObstacles: ArrayBuffer[Obstacle] = new ArrayBuffer[Obstacle]()
  var squareWidth: Float = 0
  var isClean: Boolean = false
  //var squareCoordinate: Array[Array[Coordinate]] = Array.ofDim(ROOM_HEIGHT, ROOM_WIDTH)
  init()

  def createRoom(): Unit

  def monsterAttack(c: Vector2d): Unit = {

  }

  def setFirstDraw(f: Boolean): Unit = {
    firstDraw = f
  }

  private def init(): Unit = {
    val SCREEN_WIDTH = Screen.WIDTH.toDouble
    val SCREEN_HEIGHT = Screen.HEIGHT.toDouble

    val sizeByX: Double = SCREEN_WIDTH / nbrSquareX
    val sizeByY: Double = SCREEN_HEIGHT / (nbrSquareY - 0.4)
    var height: Double = 0
    var width: Double = 0

    if (sizeByX <= sizeByY) {
      squareWidth = sizeByX.toFloat
      height = squareWidth * nbrSquareY
      width = SCREEN_WIDTH
      spaceHeight = (SCREEN_HEIGHT - height) / 2
    } else {
      squareWidth = sizeByY.toFloat
      height = SCREEN_HEIGHT
      width = squareWidth * nbrSquareX
      spaceWidth = (SCREEN_WIDTH - width) / 2
    }

    ROOM_CENTER = new Vector2d(squareWidth * nbrSquareX / 2.0.toFloat + spaceWidth.toFloat - squareWidth / 2.0.toFloat, squareWidth * nbrSquareY / 2.0.toFloat + spaceHeight.toFloat - squareWidth / 2.0.toFloat)
    ROOM_WEST = new Vector2d(squareWidth * 2 + spaceWidth.toFloat, squareWidth * nbrSquareY / 2.0.toFloat + spaceHeight.toFloat - squareWidth / 2.0.toFloat)
    ROOM_EAST = new Vector2d(squareWidth * (nbrSquareX - 2) + spaceWidth.toFloat - squareWidth, squareWidth * nbrSquareY / 2.0.toFloat + spaceHeight.toFloat - squareWidth / 2.0.toFloat)
    ROOM_NORTH = new Vector2d(squareWidth * nbrSquareX / 2.0.toFloat + spaceWidth.toFloat - squareWidth / 2.0.toFloat, squareWidth * (nbrSquareY - 2) + spaceHeight.toFloat - squareWidth)
    ROOM_SOUTH = new Vector2d(squareWidth * nbrSquareX / 2.0.toFloat + spaceWidth.toFloat - squareWidth / 2.0.toFloat, squareWidth * 2 + spaceHeight.toFloat)

    /*for (y: Int <- 0 until nbrSquareY) {
      for (x: Int <- 0 until nbrSquareX) {
        var posX: Float = (x.toDouble * squareWidth).toFloat + spaceWidth.toFloat
        var posY: Float = ((nbrSquareY - 1 - y).toDouble * squareWidth).toFloat + spaceHeight.toFloat

        squareCoordinate(y)(x) = Coordinate(posX, posY)
      }
    }*/
  }

  def wallContact(heroHitbox: Hitbox): ArrayBuffer[Direction] = {
    var res: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
    var lastObstaclePos: Vector2d = new Vector2d(10,10)

    for(obstacle <- roomObstacles) {
      if (heroHitbox.intersect(obstacle.hitbox)){
        res.addOne(heroHitbox.neighborDirection(obstacle.hitbox))
        if(lastObstaclePos.x != obstacle.position.x && lastObstaclePos.y == obstacle.position.y){
          res = res.diff(ArrayBuffer(Direction.WEST, Direction.EAST))
        }
        lastObstaclePos = obstacle.position
      }
    }
    return res
  }

  def doorContact(heroHitbox: Hitbox): Direction = {
    var res: Direction = Direction.NULL
    for(door: Door <- roomDoors) {
      if (heroHitbox.intersect(door.hitbox)){
        return door.dir
      }
    }
    return res
  }

  def doorAnimate(elapsedTime: Double): Unit = {
    if(isClean) {
      var frameTime = FRAME_TIME / speed
      dt += elapsedTime
      if (dt > frameTime) {
        dt -= frameTime

        if(curentDoorFrame < DOOR_FRAME_NUMBER - 1) {
          curentDoorFrame += 1
        }
      }
    }
  }

  override def draw(g: GdxGraphics): Unit = {
    //Mettre les différents murs
    //Ajouter les portes (trou pour les portes sur les coté et en bas). Porte du haut avec animation (déterminer si elle doit être ouverte ou fermée)
    //Ajouter le sol et les obstacles
    //Sol aléatoire ? En fonction de l'étage ? En fonction du type de salle ?
    //Affiche le perso dans la salle

    //new Texture(Gdx.files.internal("res/lib/logo_hes.png"))
    //texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)

    val wallTop = new Texture(Gdx.files.local("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_top.png"))
    val wallLeft = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_left.png"))
    val wallBot = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_bot.png"))
    val wallRight = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_right.png"))
    val wallTopCornerLeft = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_topCorner_left.png"))
    val wallTopCornerRight = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_topCorner_right.png"))
    val wallBotCornerLeft = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_botCorner_left.png"))
    val wallBotCornerRight = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_1_botCorner_right.png"))
    val wallOverTop = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_top_1.png"))
    val wallOverLeft = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_top_left.png"))
    val wallOverBot = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_bot_1.png"))
    val wallOverRight = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_top_right.png"))
    val wallOverTop_cornerLeft = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_top_inner_left.png"))
    val wallOverTop_cornerRight = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_top_inner_right.png"))
    val wallOverBot_cornerLeft = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_bot_inner_left.png"))
    val wallOverBot_cornerRight = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\wall_bot_inner_right.png"))
    val floor = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\floor\\floor_2.png"))
    val obstacle = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\props_itens\\barrel.png"))

    if(firstDraw){
      monsters = new ArrayBuffer[Monster]()
    }

    for (y: Int <- 0 until nbrSquareY) {
      for (x: Int <- 0 until nbrSquareX) {
        var posX: Float = (x.toDouble * squareWidth).toFloat + spaceWidth.toFloat
        var posY: Float = ((nbrSquareY - 1 - y).toDouble * squareWidth).toFloat + spaceHeight.toFloat

        if (y == 1 && x >= 2 && x < nbrSquareX - 2) {
          if(firstDraw){
            roomObstacles.append(new Obstacle(new Vector2d(posX, posY), squareWidth))
          }
          g.draw(wallTop, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if(x == ROOM_WIDTH/2+2 && doorsPositions.contains(Direction.NORTH)) {
            if(firstDraw){
              roomDoors.append(Door(new Hitbox(new Vector2d(posX + squareWidth/2, posY + squareWidth/2),
                squareWidth, squareWidth), Direction.NORTH))
            }
            g.draw(doorTopSs.sprites(0)(curentDoorFrame), posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
        }
        else if (x == 1 && y >= 2 && y < nbrSquareY - 2) {
          if(firstDraw){
            roomObstacles.append(new Obstacle(new Vector2d(posX, posY), squareWidth))
          }
          g.draw(wallLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if (y == ROOM_HEIGHT/2+2 && doorsPositions.contains(Direction.WEST)) {
            if(firstDraw){
              roomDoors.append(Door(new Hitbox(new Vector2d(posX + squareWidth / 2, posY + squareWidth / 2),
                squareWidth, squareWidth), Direction.WEST))
            }
            g.draw(doorLeftSs.sprites(curentDoorFrame)(0), posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
        }
        else if (y == nbrSquareY - 2 && x >= 2 && x < nbrSquareX - 2) {
          if(firstDraw){
            roomObstacles.append(new Obstacle(new Vector2d(posX, posY), squareWidth))
          }
          g.draw(wallBot, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if (x == ROOM_WIDTH/2+2 && doorsPositions.contains(Direction.SOUTH)) {
            if(firstDraw){
              roomDoors.append(Door(new Hitbox(new Vector2d(posX + squareWidth / 2, posY + squareWidth / 2),
                squareWidth, squareWidth), Direction.SOUTH))
            }
            g.draw(doorBotSs.sprites(0)(curentDoorFrame), posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
        }
        else if (x == nbrSquareX - 2 && y >= 2 && y < nbrSquareY - 2) {
          if(firstDraw){
            roomObstacles.append(new Obstacle(new Vector2d(posX, posY), squareWidth))
          }
          g.draw(wallRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if (y == ROOM_HEIGHT/2+2 && doorsPositions.contains(Direction.EAST)) {
            if(firstDraw){
              roomDoors.append(Door(new Hitbox(new Vector2d(posX + squareWidth / 2, posY + squareWidth / 2),
                squareWidth, squareWidth), Direction.EAST))
            }
            g.draw(doorRightSs.sprites(curentDoorFrame)(0), posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
        }
        else if (x == 0 && y != 0 && y < nbrSquareY - 1) {
          g.draw(wallOverLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (x == nbrSquareX - 1 && y != 0 && y < nbrSquareY - 1) {
          g.draw(wallOverRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (y == 0 && x != 0 && x < nbrSquareX - 1) {
          g.draw(wallOverTop, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (y == nbrSquareY - 1 && x != 0 && x < nbrSquareX - 1) {
          g.draw(wallOverBot, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (x == 0 && y == nbrSquareY - 1) {
          g.draw(wallOverBot_cornerLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (x == nbrSquareX - 1 && y == nbrSquareY - 1) {
          g.draw(wallOverBot_cornerRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (x == 0 && y == 0) {
          g.draw(wallOverTop_cornerLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (x == nbrSquareX - 1 && y == 0) {
          g.draw(wallOverTop_cornerRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if(x == 1 && y == 1) {
          g.draw(wallTopCornerLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if(x == nbrSquareX - 2 && y == 1) {
          g.draw(wallTopCornerRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (x == 1 && y == nbrSquareY - 2) {
          g.draw(wallBotCornerLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if (x == nbrSquareX - 2 && y == nbrSquareY - 2) {
          g.draw(wallBotCornerRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
        }
        else if(y >= 2 && y < nbrSquareY - 2 && x >= 2 && x < nbrSquareX - 2) {
          if(firstDraw) {
            /*val centerX: Float = posX + squareWidth / 2
            val centerY: Float = posY + squareWidth / 2*/
            roomVectors(y - 2)(x - 2) = new Vector2d(posX, posY)
          }
          g.draw(floor, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if (room(y - 2)(x - 2) == ROOM_OBSTACLE) {
            if(firstDraw){
              roomObstacles.append(new Obstacle(new Vector2d(posX, posY), squareWidth))
            }
            g.draw(obstacle, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
          else if (room(y - 2)(x - 2) == ROOM_MONSTER) {
            if(firstDraw && !isClean){
              monsters.append(new Monster(new Vector2d(posX, posY), squareWidth))
            }
          }
          else if (room(y - 2)(x - 2) == ROOM_CHARACTER) {

          }
        }
      }
    }

    if(monsters.isEmpty){
      isClean = true
    }

    firstDraw = false
    wallTop.dispose()
    wallLeft.dispose()
    wallBot.dispose()
    wallRight.dispose()
    wallTopCornerLeft.dispose()
    wallTopCornerRight.dispose()
    wallBotCornerLeft.dispose()
    wallBotCornerRight.dispose()
    wallOverTop.dispose()
    wallOverLeft.dispose()
    wallOverBot.dispose()
    wallOverRight.dispose()
    wallOverTop_cornerLeft.dispose()
    wallOverTop_cornerRight.dispose()
    wallOverBot_cornerLeft.dispose()
    wallOverBot_cornerRight.dispose()
    floor.dispose()
    obstacle.dispose()
  }
}