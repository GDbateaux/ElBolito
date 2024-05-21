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

  private val NUMBER_OBSTACLE_PERCENTAGE = 20
  private val OBSTACLE_SIZE_MAX = 5

  private val NORTH: Int = 0
  private val WEST: Int = 1
  private val SOUTH: Int = 2
  private val EAST: Int = 3

  private val difficulty = roomNbr + floorNbr;
  private var room: Array[Array[Int]] = Array.ofDim(ROOM_HEIGHT, ROOM_WIDTH)
  //private var monsters: ArrayBuffer[Monster] = new ArrayBuffer[Monster]()

  def createRoom(): Unit = {
    generateMonsters()

    //Le character est devant la porte d'entrée
    if(doorEnterPos == NORTH) {
      room(0)((ROOM_WIDTH - 1 / 2)) = ROOM_CHARACTER
    }
    else if(doorEnterPos == WEST) {
      room((ROOM_HEIGHT - 1) / 2)(ROOM_WIDTH) = ROOM_CHARACTER
    }
    else if (doorEnterPos == SOUTH) {
      room(ROOM_HEIGHT)((ROOM_WIDTH - 1 / 2)) = ROOM_CHARACTER
    }
    else if (doorEnterPos == EAST) {
      room((ROOM_HEIGHT - 1) / 2)(0) = ROOM_CHARACTER
    }

    //Les obstacles sont génééré aléatoirement
    //Les obstacles ne bloque pas les porte
    //Les obstacles peuvent être de simple à 5
    //Il y a un maximum de pixel que les obstacle peuvent prendre en fonction des dimensions de la room
    //Les obstacles doivent être écarté les un des autres (1 carré d'écart)
    var obstaclesRemain = ROOM_WIDTH * ROOM_HEIGHT / 100 * NUMBER_OBSTACLE_PERCENTAGE

    while(obstaclesRemain > 0) {
      var obstacleSize = Random.nextInt(OBSTACLE_SIZE_MAX + 1)



      obstaclesRemain = obstaclesRemain - obstacleSize
    }

  }

  def generateMonsters(): Unit = {
    // TODO
  }
}
