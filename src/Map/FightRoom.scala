package Map

import Characters.{Enemy, Hero, Monster}
import Utils.{Direction, Position, Vector2d}
import Utils.Direction.Direction
import ch.hevs.gdx2d.lib.GdxGraphics

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class FightRoom(val diffulty: Int, val doorsDir: ArrayBuffer[Direction]) extends Room {
  private val NUMBER_OBSTACLE_MAX_PERCENTAGE = 20
  private val NUMBER_OBSTACLE_MIN_PERCENTAGE = 15
  private val OBSTACLE_SIZE_MAX = 5
  private val DIFFICULTY_MAX = 1
  var characterDir: Direction = Direction.NORTH

  isClean = false
  doorsPositions = doorsDir
  curentDoorFrame = 0
  createRoom()

  override def createRoom(): Unit = {
      //generateMonsters()

      if (characterDir == Direction.NORTH) {
        room(0)((ROOM_WIDTH - 1) / 2) = ROOM_CHARACTER
      }
      else if (characterDir == Direction.EAST) {
        room((ROOM_HEIGHT - 1) / 2)(ROOM_WIDTH - 1) = ROOM_CHARACTER
      }
      else if (characterDir == Direction.SOUTH) {
        room(ROOM_HEIGHT - 1)((ROOM_WIDTH - 1) / 2) = ROOM_CHARACTER
      }
      else if (characterDir == Direction.WEST) {
        room((ROOM_HEIGHT - 1) / 2)(0) = ROOM_CHARACTER
      }

      //Les obstacles sont génééré aléatoirement
      //Les obstacles ne bloque pas les porte
      //Les obstacles peuvent être de simple à 5
      //Il y a un maximum de pixel que les obstacle peuvent prendre en fonction des dimensions de la room
      //Les obstacles doivent être écarté les un des autres (1 carré d'écart)
      val nbrObstaclePercentage = Random.nextInt(NUMBER_OBSTACLE_MAX_PERCENTAGE - NUMBER_OBSTACLE_MIN_PERCENTAGE + 1) + NUMBER_OBSTACLE_MIN_PERCENTAGE
      var obstaclesRemain = ROOM_WIDTH * ROOM_HEIGHT / 100 * nbrObstaclePercentage

      var obstaclesPositionRemain: ArrayBuffer[Position] = new ArrayBuffer[Position]()
      var monstersPositionRemain: ArrayBuffer[Position] = new ArrayBuffer[Position]()
      for (y <- 0 until ROOM_HEIGHT) {
        for (x <- 0 until ROOM_WIDTH) {
          obstaclesPositionRemain.addOne(Position(x, y))
        }
      }

      for (doorDir <- doorsDir) {
        var doorPos: Position = Position(0, 0)
        if (doorDir == Direction.NORTH) {
          doorPos = Position((ROOM_WIDTH - 1) / 2, 0)
        }
        else if (doorDir == Direction.EAST) {
          doorPos = Position(ROOM_WIDTH - 1, (ROOM_HEIGHT - 1) / 2)
        }
        else if (doorDir == Direction.SOUTH) {
          doorPos = Position((ROOM_WIDTH - 1) / 2, ROOM_HEIGHT - 1)
        }
        else if (doorDir == Direction.WEST) {
          doorPos = Position(0, (ROOM_HEIGHT - 1) / 2)
        }

        //Remove doors and neighbors positions
        obstaclesPositionRemain.subtractOne(doorPos)
        for (posToRemove <- getNeighbor(obstaclesPositionRemain, doorPos)) {
          obstaclesPositionRemain.subtractOne(posToRemove)
        }
      }

      monstersPositionRemain = obstaclesPositionRemain.clone()

      //Aléatoire et symétrique
      while (obstaclesRemain > 0) {
        var obstacleSize = Random.nextInt(OBSTACLE_SIZE_MAX) + 1
        if (OBSTACLE_SIZE_MAX > obstaclesRemain) {
          obstacleSize = Random.nextInt(obstaclesRemain) + 1
        }
        obstaclesRemain = obstaclesRemain - obstacleSize

        var obstaclesPos: ArrayBuffer[Position] = new ArrayBuffer[Position]()

        if (obstaclesPositionRemain.isEmpty) {
          obstaclesRemain = 0
        }
        else {
          // First Wall
          var newPositionId = Random.nextInt(obstaclesPositionRemain.length)
          var pos = obstaclesPositionRemain(newPositionId)
          obstaclesPos.addOne(pos)
          obstaclesPositionRemain.subtractOne(pos)
          room(pos.y)(pos.x) = ROOM_OBSTACLE
          obstacleSize = obstacleSize - 1

          //Other walls
          while (obstacleSize > 0) {
            newPositionId = Random.nextInt(obstaclesPos.length)
            var posPossible: ArrayBuffer[Position] = getNeighbor(obstaclesPositionRemain, obstaclesPos(newPositionId))
            if (posPossible.nonEmpty) {3
              newPositionId = Random.nextInt(posPossible.length)
              pos = posPossible(newPositionId)
              obstaclesPos.addOne(pos)

              obstaclesPositionRemain.subtractOne(pos)
              monstersPositionRemain.subtractOne(pos)

              room(pos.y)(pos.x) = ROOM_OBSTACLE
              obstacleSize = obstacleSize - 1
            }
            else {
              obstaclesRemain = obstaclesRemain + obstacleSize
              obstacleSize = 0
            }
          }

          //Remove wall neighbor
          for (obstacle <- obstaclesPos) {
            for (posToRemove <- getNeighbor(obstaclesPositionRemain, obstacle)) {
              obstaclesPositionRemain.subtractOne(posToRemove)
            }
          }
        }
      }
      generateMonsters(monstersPositionRemain)
    }

  override def monsterAttack(c: Vector2d): Unit = {
    for (m <- monsters) {
      m.go(c)
    }
  }

    private def getNeighbor(posPossible: ArrayBuffer[Position], pos: Position): ArrayBuffer[Position] = {
      val res: ArrayBuffer[Position] = new ArrayBuffer[Position]()

      if (posPossible.contains(Position(pos.x, pos.y - 1))) {
        res.append(Position(pos.x, pos.y - 1))
      }
      if (posPossible.contains(Position(pos.x - 1, pos.y))) {
        res.append(Position(pos.x - 1, pos.y))
      }
      if (posPossible.contains(Position(pos.x, pos.y + 1))) {
        res.append(Position(pos.x, pos.y + 1))
      }
      if (posPossible.contains(Position(pos.x + 1, pos.y))) {
        res.append(Position(pos.x + 1, pos.y))
      }
      if (posPossible.contains(Position(pos.x - 1, pos.y - 1))) {
        res.append(Position(pos.x - 1, pos.y - 1))
      }
      if (posPossible.contains(Position(pos.x + 1, pos.y + 1))) {
        res.append(Position(pos.x + 1, pos.y + 1))
      }
      if (posPossible.contains(Position(pos.x + 1, pos.y - 1))) {
        res.append(Position(pos.x + 1, pos.y - 1))
      }
      if (posPossible.contains(Position(pos.x - 1, pos.y + 1))) {
        res.append(Position(pos.x - 1, pos.y + 1))
      }

      return res
  }

  private def generateMonsters(posPossible: ArrayBuffer[Position]): Unit = {
    var diffultyRemain = diffulty
    while(diffultyRemain > 0) {
      if(posPossible.nonEmpty) {
        var newPositionId: Int = Random.nextInt(posPossible.length)
        var pos: Position = posPossible(newPositionId)
        posPossible.subtractOne(pos)
        room(pos.y)(pos.x) = ROOM_MONSTER

        var newDifficulty: Int = Random.nextInt(DIFFICULTY_MAX) + 1
        if (DIFFICULTY_MAX > diffultyRemain) {
          newDifficulty = Random.nextInt(diffultyRemain) + 1
        }

        diffultyRemain = diffultyRemain - newDifficulty
      } else {
        diffultyRemain = 0
      }
    }
  }

  override def manageRoom(h: Hero): Unit = {
    var idx:Int = 0
    while (idx < monsters.length) {
      monsters(idx).asInstanceOf[Monster].manageMonster(h, room, roomVectors, squareWidth)
      monsters(idx).asInstanceOf[Monster].setSpeed(0.6)
      if (monsters(idx).hp <= 0) {
        monsters.subtractOne(monsters(idx))
      }
      idx += 1
    }

    if(monsters.isEmpty){
      isClean = true
    }
  }

  override def draw(g: GdxGraphics): Unit = {
    super.draw(g)
    for(m: Enemy <- monsters){
      m.draw(g)
    }
  }
}

/*
object RoomTest extends App {
  private var doors: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
  doors.addOne(Direction.NORTH)
  doors.addOne(Direction.WEST)
  doors.addOne(Direction.EAST)
  private val r: FightRoom = new FightRoom(20, Direction.WEST, doors)
  r.createRoom()
}
 */