package Map

import Utils.Direction
import Utils.Direction.Direction
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

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

  def createRoom(): Unit;

  override def draw(g: GdxGraphics): Unit = {
    //Mettre les différents murs
    //Ajouter les portes (trou pour les portes sur les coté et en bas). Porte du haut avec animation (déterminer si elle doit être ouverte ou fermée)
    //Ajouter le sol et les obstacles
    //Sol aléatoire ? En fonction de l'étage ? En fonction du type de salle ?
    //Affiche le perso dans la salle

    //new Texture(Gdx.files.internal("res/lib/logo_hes.png"));
    //texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)

    val nbrSquareX: Int = ROOM_WIDTH + 4 // 2 = les murs droites et gauches (+ over)
    val nbrSquareY: Int = ROOM_HEIGHT + 4 // 2 = les murs en haut et en bas (+ over)
    val sizeByX: Double = g.getScreenWidth.toDouble / nbrSquareX
    val sizeByY: Double = g.getScreenHeight.toDouble / (nbrSquareY - 0.4)
    var pixelSize: Double = 0;
    var height: Double = 0;
    var width: Double = 0;
    var spaceWidth: Double = 0;
    var spaceHeight: Double = 0;

    if(sizeByX <= sizeByY) {
      pixelSize = sizeByX;
      height = pixelSize * nbrSquareY;
      width = g.getScreenWidth;
      spaceHeight = (g.getScreenHeight - height) / 2;
    } else {
      pixelSize = sizeByY;
      height = g.getScreenHeight;
      width = pixelSize * nbrSquareX;
      spaceWidth = (g.getScreenWidth - width) / 2;
    }

    var wallTop = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_top.png"))
    var wallLeft = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_left.png"))
    var wallBot = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_bot.png"))
    var wallRight = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_right.png"))
    var wallTopCornerLeft = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_topCorner_left.png"))
    var wallTopCornerRight = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_topCorner_right.png"))
    var wallBotCornerLeft = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_botCorner_left.png"))
    var wallBotCornerRight = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_1_botCorner_right.png"))
    var wallOverTop = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_top_1.png"))
    var wallOverLeft = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_top_left.png"))
    var wallOverBot = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_bot_1.png"))
    var wallOverRight = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_top_right.png"))
    var wallOverTop_cornerLeft = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_top_inner_left.png"))
    var wallOverTop_cornerRight = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_top_inner_right.png"))
    var wallOverBot_cornerLeft = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_bot_inner_left.png"))
    var wallOverBot_cornerRight = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\wall_bot_inner_right.png"))
    var floor = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\floor\\floor_2.png"))
    var obstacle = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\props_itens\\barrel.png"))
    var doorTop = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\door_top_closed.png"))
    var doorLeft = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\door_left_closed.png"))
    var doorBot = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\door_bot_closed.png"))
    var doorRight = new Texture(Gdx.files.absolute("C:\\Users\\simas\\Desktop\\JeuBOLI\\v1.1 dungeon crawler 16X16 pixel pack\\tiles\\wall\\door_right_closed.png"))


    for (y: Int <- 0 until nbrSquareY) {
      for (x: Int <- 0 until nbrSquareX) {
        var posX: Float = (x.toDouble * pixelSize).toFloat + spaceWidth.toFloat;
        var posY: Float = ((nbrSquareY - 1 - y).toDouble * pixelSize).toFloat + spaceHeight.toFloat;

        if (y == 1 && x >= 2 && x < nbrSquareX - 2) {
          g.draw(wallTop, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          if(x == 11 && doorsPositions.contains(Direction.NORTH)) {
            g.draw(doorTop, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          }
        }
        else if (x == 1 && y >= 2 && y < nbrSquareY - 2) {
          g.draw(wallLeft, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          if (y == 7 && doorsPositions.contains(Direction.WEST)) {
            g.draw(doorLeft, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          }
        }
        else if (y == nbrSquareY - 2 && x >= 2 && x < nbrSquareX - 2) {
          g.draw(wallBot, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          if (x == 11 && doorsPositions.contains(Direction.SOUTH)) {
            g.draw(doorBot, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          }
        }
        else if (x == nbrSquareX - 2 && y >= 2 && y < nbrSquareY - 2) {
          g.draw(wallRight, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          if (y == 7 && doorsPositions.contains(Direction.EAST)) {
            g.draw(doorRight, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
          }
        }
        else if (x == 0 && y != 0 && y < nbrSquareY - 1) {
          g.draw(wallOverLeft, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (x == nbrSquareX - 1 && y != 0 && y < nbrSquareY - 1) {
          g.draw(wallOverRight, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (y == 0 && x != 0 && x < nbrSquareX - 1) {
          g.draw(wallOverTop, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (y == nbrSquareY - 1 && x != 0 && x < nbrSquareX - 1) {
          g.draw(wallOverBot, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (x == 0 && y == nbrSquareY - 1) {
          g.draw(wallOverBot_cornerLeft, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (x == nbrSquareX - 1 && y == nbrSquareY - 1) {
          g.draw(wallOverBot_cornerRight, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (x == 0 && y == 0) {
          g.draw(wallOverTop_cornerLeft, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (x == nbrSquareX - 1 && y == 0) {
          g.draw(wallOverTop_cornerRight, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if(x == 1 && y == 1) {
          g.draw(wallTopCornerLeft, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if(x == nbrSquareX - 2 && y == 1) {
          g.draw(wallTopCornerRight, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (x == 1 && y == nbrSquareY - 2) {
          g.draw(wallBotCornerLeft, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if (x == nbrSquareX - 2 && y == nbrSquareY - 2) {
          g.draw(wallBotCornerRight, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
        }
        else if(y >= 2 && y < nbrSquareY - 2 && x >= 2 && x < nbrSquareX - 2) {
          g.draw(floor, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
           if (room(y - 2)(x - 2) == ROOM_OBSTACLE) {
            g.draw(obstacle, posX, posY, pixelSize.toFloat, pixelSize.toFloat)
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

