package Map

import Characters.{Hero, Monster}
import Utils.{Coordinate, Direction, Screen}
import Utils.Direction.Direction
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

import scala.collection.mutable.ArrayBuffer

//Type de room: 0: Chill, 1: Battle, 2: Boss, 3: BigFoot (pas sûr pour le 3)
trait Room extends DrawableObject {
  protected val ROOM_HEIGHT: Int = 11
  protected val ROOM_WIDTH: Int = 19

  protected val ROOM_CHARACTER: Int = 1
  protected val ROOM_MONSTER: Int = 2
  protected val ROOM_OBSTACLE: Int = 3

  protected var room: Array[Array[Int]] = Array.ofDim(ROOM_HEIGHT, ROOM_WIDTH)
  protected var doorsPositions: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
  protected var monsters: ArrayBuffer[Monster] = new ArrayBuffer[Monster]()

  private val nbrSquareX: Int = ROOM_WIDTH + 4 // 2 = les murs droites et gauches (+ over)
  private val nbrSquareY: Int = ROOM_HEIGHT + 4 // 2 = les murs en haut et en bas (+ over)
  private var spaceWidth: Double = 0
  private var spaceHeight: Double = 0

  var squareWidth: Float = 0
  init()

  def createRoom(): Unit

  def monsterAttack(c: Coordinate): Unit = {

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
    val doorTop = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\door_top_closed.png"))
    val doorLeft = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\door_left_closed.png"))
    val doorBot = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\door_bot_closed.png"))
    val doorRight = new Texture(Gdx.files.absolute("data\\images\\dungeonTextures\\tiles\\wall\\door_right_closed.png"))


    for (y: Int <- 0 until nbrSquareY) {
      for (x: Int <- 0 until nbrSquareX) {
        var posX: Float = (x.toDouble * squareWidth).toFloat + spaceWidth.toFloat
        var posY: Float = ((nbrSquareY - 1 - y).toDouble * squareWidth).toFloat + spaceHeight.toFloat

        if (y == 1 && x >= 2 && x < nbrSquareX - 2) {
          g.draw(wallTop, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if(x == 11 && doorsPositions.contains(Direction.NORTH)) {
            g.draw(doorTop, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
        }
        else if (x == 1 && y >= 2 && y < nbrSquareY - 2) {
          g.draw(wallLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if (y == 7 && doorsPositions.contains(Direction.WEST)) {
            g.draw(doorLeft, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
        }
        else if (y == nbrSquareY - 2 && x >= 2 && x < nbrSquareX - 2) {
          g.draw(wallBot, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if (x == 11 && doorsPositions.contains(Direction.SOUTH)) {
            g.draw(doorBot, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
        }
        else if (x == nbrSquareX - 2 && y >= 2 && y < nbrSquareY - 2) {
          g.draw(wallRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          if (y == 7 && doorsPositions.contains(Direction.EAST)) {
            g.draw(doorRight, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
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
          g.draw(floor, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
           if (room(y - 2)(x - 2) == ROOM_OBSTACLE) {
            g.draw(obstacle, posX, posY, squareWidth.toFloat, squareWidth.toFloat)
          }
          else if (room(y - 2)(x - 2) == ROOM_MONSTER) {

          }
          else if (room(y - 2)(x - 2) == ROOM_CHARACTER) {

          }
        }
      }
    }

  }

  def openDoor(): Unit = {

  }
}
