package Map

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

//Type de room: 0: Chill, 1: Battle, 2: Boss, 3: BigFoot (pas sûr pour le 3)
//Room door: 0: NORTH, 1: WEST, 2: SOUTH, 3: EAST
class Room(val roomNbr: Int, val floorNbr: Int, val roomType: Int, val doorEnterPos: Int, val doorExitPos: Int) {
  private val ROOM_HEIGHT: Int = 11
  private val ROOM_WIDTH: Int = 19

  private val ROOM_CHARACTER: Int = 1
  private val ROOM_MONSTER: Int = 2
  private val ROOM_OBSTACLE: Int = 3

  private val NUMBER_OBSTACLE_MAX_PERCENTAGE = 20
  private val NUMBER_OBSTACLE_MIN_PERCENTAGE = 15
  private val OBSTACLE_SIZE_MAX = 5

  private val NORTH: Int = 0
  private val WEST: Int = 1
  private val SOUTH: Int = 2
  private val EAST: Int = 3

  private val difficulty = roomNbr + floorNbr;
  private var room: Array[Array[Int]] = Array.ofDim(ROOM_HEIGHT, ROOM_WIDTH)
  //private var monsters: ArrayBuffer[Monster] = new ArrayBuffer[Monster]()

  def createRoom(): Unit = {
    //generateMonsters()

    //Le character est devant la porte d'entrée
    var characterPos: Position = Position(0, 0);
    var exitDoorPos: Position = Position(0, 0);
    if(doorEnterPos == NORTH) {
      room(0)((ROOM_WIDTH - 1) / 2) = ROOM_CHARACTER
      characterPos = Position((ROOM_WIDTH - 1) / 2, 0);
      exitDoorPos = Position((ROOM_WIDTH - 1) / 2, ROOM_HEIGHT - 1);
    }
    else if(doorEnterPos == WEST) {
      room((ROOM_HEIGHT - 1) / 2)(ROOM_WIDTH - 1) = ROOM_CHARACTER
      characterPos = Position(ROOM_WIDTH - 1, (ROOM_HEIGHT - 1) / 2);
      exitDoorPos = Position(0, (ROOM_HEIGHT - 1) / 2);
    }
    else if (doorEnterPos == SOUTH) {
      room(ROOM_HEIGHT - 1)((ROOM_WIDTH - 1) / 2) = ROOM_CHARACTER
      characterPos = Position((ROOM_WIDTH - 1) / 2, ROOM_HEIGHT - 1);
      exitDoorPos = Position((ROOM_WIDTH - 1) / 2, 0);
    }
    else if (doorEnterPos == EAST) {
      room((ROOM_HEIGHT - 1) / 2)(0) = ROOM_CHARACTER
      characterPos = Position(0, (ROOM_HEIGHT - 1) / 2);
      exitDoorPos = Position(ROOM_WIDTH - 1, (ROOM_HEIGHT - 1) / 2);
    }

    //Les obstacles sont génééré aléatoirement
    //Les obstacles ne bloque pas les porte
    //Les obstacles peuvent être de simple à 5
    //Il y a un maximum de pixel que les obstacle peuvent prendre en fonction des dimensions de la room
    //Les obstacles doivent être écarté les un des autres (1 carré d'écart)
    val nbrObstaclePercentage = Random.nextInt(NUMBER_OBSTACLE_MAX_PERCENTAGE - NUMBER_OBSTACLE_MIN_PERCENTAGE + 1) + NUMBER_OBSTACLE_MIN_PERCENTAGE;
    var obstaclesRemain = ROOM_WIDTH * ROOM_HEIGHT / 100 * nbrObstaclePercentage

    var positionRemain: ArrayBuffer[Position] = new ArrayBuffer[Position]();
    for(y <- 0 until ROOM_HEIGHT) {
      for(x <-0 until ROOM_WIDTH) {
        positionRemain.addOne(Position(x, y));
      }
    }

    //Remove Character and neighbors positions
    positionRemain.subtractOne(characterPos);
    for (posToRemove <- getNeighbor(positionRemain, characterPos)) {
      positionRemain.subtractOne(posToRemove);
    }

    //Remove Exit Door and neighbors positions
    positionRemain.subtractOne(exitDoorPos);
    for (posToRemove <- getNeighbor(positionRemain, exitDoorPos)) {
      positionRemain.subtractOne(posToRemove);
    }

    //Aléatoire et symétrique
    while(obstaclesRemain > 0) {
      var obstacleSize = Random.nextInt(OBSTACLE_SIZE_MAX) + 1
      if(OBSTACLE_SIZE_MAX > obstaclesRemain) {
        obstacleSize = Random.nextInt(obstaclesRemain) + 1
      }
      obstaclesRemain = obstaclesRemain - obstacleSize

      var obstaclesPos: ArrayBuffer[Position] = new ArrayBuffer[Position]();

      if(positionRemain.isEmpty) {
         obstaclesRemain = 0;
      }
      else {
        // First Wall
        var newPositionId = Random.nextInt(positionRemain.length)
        var pos = positionRemain(newPositionId);
        obstaclesPos.addOne(pos);
        positionRemain.subtractOne(pos);
        room(pos.y)(pos.x) = ROOM_OBSTACLE;
        obstacleSize = obstacleSize - 1;

        //Other walls
        while (obstacleSize > 0) {
          newPositionId = Random.nextInt(obstaclesPos.length);
          var posPossible: ArrayBuffer[Position] = getNeighbor(positionRemain, obstaclesPos(newPositionId));
          if (posPossible.nonEmpty) {
            newPositionId = Random.nextInt(posPossible.length);
            pos = posPossible(newPositionId);
            obstaclesPos.addOne(pos);
            positionRemain.subtractOne(pos);
            room(pos.y)(pos.x) = ROOM_OBSTACLE;
            obstacleSize = obstacleSize - 1;
          }
          else {
            obstaclesRemain = obstaclesRemain + obstacleSize;
            obstacleSize = 0;
          }
        }

        //Remove wall neighbor
        for (obstacle <- obstaclesPos) {
          for (posToRemove <- getNeighbor(positionRemain, obstacle)) {
            positionRemain.subtractOne(posToRemove);
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
      res.append(Position(pos.x - 1, pos.y - 1));
    }
    if (posPossible.contains(Position(pos.x + 1, pos.y + 1))) {
      res.append(Position(pos.x + 1, pos.y + 1));
    }
    if (posPossible.contains(Position(pos.x + 1, pos.y - 1))) {
      res.append(Position(pos.x + 1, pos.y - 1));
    }
    if (posPossible.contains(Position(pos.x - 1, pos.y + 1))) {
      res.append(Position(pos.x - 1, pos.y + 1));
    }

    return res
  }

  def generateMonsters(): Unit = {
    // TODO
  }
}

object RoomTest extends App {
  val r: Room = new Room(2, 1, 1, 2, 0)
  r.createRoom()
}
