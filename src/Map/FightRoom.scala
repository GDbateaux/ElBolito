package Map

import Utils.{Direction, Position}
import Utils.Direction.Direction

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class FightRoom(val diffulty: Int, val doorsDir: ArrayBuffer[Direction]) extends Room {

  private val NUMBER_OBSTACLE_MAX_PERCENTAGE = 20
  private val NUMBER_OBSTACLE_MIN_PERCENTAGE = 15
  private val OBSTACLE_SIZE_MAX = 5
  var characterDir: Direction = Direction.NORTH
  createRoom()

  override def createRoom(): Unit = {
      //generateMonsters()

      doorsPositions = doorsDir

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

      var positionRemain: ArrayBuffer[Position] = new ArrayBuffer[Position]()
      for (y <- 0 until ROOM_HEIGHT) {
        for (x <- 0 until ROOM_WIDTH) {
          positionRemain.addOne(Position(x, y))
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
        positionRemain.subtractOne(doorPos)
        for (posToRemove <- getNeighbor(positionRemain, doorPos)) {
          positionRemain.subtractOne(posToRemove)
        }
      }

      //Aléatoire et symétrique
      while (obstaclesRemain > 0) {
        var obstacleSize = Random.nextInt(OBSTACLE_SIZE_MAX) + 1
        if (OBSTACLE_SIZE_MAX > obstaclesRemain) {
          obstacleSize = Random.nextInt(obstaclesRemain) + 1
        }
        obstaclesRemain = obstaclesRemain - obstacleSize

        var obstaclesPos: ArrayBuffer[Position] = new ArrayBuffer[Position]()

        if (positionRemain.isEmpty) {
          obstaclesRemain = 0
        }
        else {
          // First Wall
          var newPositionId = Random.nextInt(positionRemain.length)
          var pos = positionRemain(newPositionId)
          obstaclesPos.addOne(pos)
          positionRemain.subtractOne(pos)
          room(pos.y)(pos.x) = ROOM_OBSTACLE
          obstacleSize = obstacleSize - 1

          //Other walls
          while (obstacleSize > 0) {
            newPositionId = Random.nextInt(obstaclesPos.length)
            var posPossible: ArrayBuffer[Position] = getNeighbor(positionRemain, obstaclesPos(newPositionId))
            if (posPossible.nonEmpty) {
              newPositionId = Random.nextInt(posPossible.length)
              pos = posPossible(newPositionId)
              obstaclesPos.addOne(pos)
              positionRemain.subtractOne(pos)
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
            for (posToRemove <- getNeighbor(positionRemain, obstacle)) {
              positionRemain.subtractOne(posToRemove)
            }
          }
        }
      }

      for (y: Int <- room.indices) {
        for (x: Int <- room(0).indices) {
          print(room(y)(x))
        }
        println()
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

  def generateMonsters(): Unit = {
    // TODO
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