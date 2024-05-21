package Map

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Floor(private val numRoom: Int) {
  private var arraySide: Int = 0
  private val PRODUCT_SIDE_ROOME: Double = 0.8
  private val tmpSide: Int = (numRoom * PRODUCT_SIDE_ROOME).toInt

  if(tmpSide % 2 == 0){
    arraySide = tmpSide + 1
  }
  else{
    arraySide = tmpSide
  }

  def generateFloor(): Unit = {
    var a: Array[Array[Int]] = addRoomType(generateBlueprint())

    for(y: Int <- a.indices){
      for(x: Int <- a(0).indices){
        print(a(y)(x))
      }
      println()
    }
  }

  private def generateBlueprint(): Array[Array[Int]] ={
    val floorInt: Array[Array[Int]] = Array.ofDim(arraySide, arraySide)
    var currentNumRoom: Int = 1
    var previousRoomX: Int = arraySide/2
    var previousRoomY: Int = arraySide/2
    var currentRoomX: Int = arraySide/2
    var currentRoomY: Int = arraySide/2
    floorInt(currentRoomY)(currentRoomX) = 1

    while(currentNumRoom < numRoom){
      val rdmNbre: Int = Random.nextInt(100)
      val availableRoom: ArrayBuffer[String] = getFreeDoor(floorInt, currentRoomX, currentRoomY)

      if(availableRoom.nonEmpty){
        val direction: String = availableRoom(Random.nextInt(availableRoom.length))
        previousRoomX = currentRoomX
        previousRoomY = currentRoomY

        if(direction == "north"){
          currentRoomY -= 1
          floorInt(currentRoomY)(currentRoomX) = 1
        }
        else if(direction == "west"){
          currentRoomX -= 1
          floorInt(currentRoomY)(currentRoomX) = 1
        }
        else if (direction == "south") {
          currentRoomY += 1
          floorInt(currentRoomY)(currentRoomX) = 1
        }
        else {
          currentRoomX += 1
          floorInt(currentRoomY)(currentRoomX) = 1
        }
        currentNumRoom += 1
      }


      if(availableRoom.isEmpty){
        if (rdmNbre < 59) {
          currentRoomX = previousRoomX
          currentRoomY = previousRoomY
        }
        else {
          val rdmRoom: Array[Int] = getRandomRoom(floorInt, currentNumRoom)
          previousRoomX = currentRoomX
          previousRoomY = currentRoomY
          currentRoomX = rdmRoom(0)
          currentRoomY = rdmRoom(1)
        }
      }
      else{
        if (rdmNbre < 35) {
          currentRoomX = previousRoomX
          currentRoomY = previousRoomY
        }
        else if(rdmNbre >= 75) {
          val rdmRoom: Array[Int] = getRandomRoom(floorInt, currentNumRoom)
          previousRoomX = currentRoomX
          previousRoomY = currentRoomY
          currentRoomX = rdmRoom(0)
          currentRoomY = rdmRoom(1)
        }
      }
    }
    return floorInt
  }

  //0: No room; 1: Simple room; 2: Start room; 3: Alone room; 4: Boss room
  def addRoomType(floor: Array[Array[Int]]): Array[Array[Int]] = {
    val res: Array[Array[Int]] = floor.clone()

    for(y: Int <- floor.indices){
      for(x: Int <- floor(0).indices){
        if(getFreeDoor(floor, x, y).length == 1){
          res(y)(x) = 3
        }
      }
    }
    res(arraySide/2)(arraySide/2) = 2

    return res
  }

  private def getFreeDoor(floor: Array[Array[Int]], x: Int, y: Int): ArrayBuffer[String] = {
    val res: ArrayBuffer[String] = new ArrayBuffer[String]()

    if(y > 0 && floor(y-1)(x) == 0){
      res.append("north")
    }
    if(x > 0 && floor(y)(x-1) == 0){
      res.append("west")
    }
    if(y < arraySide - 1 && floor(y+1)(x) == 0){
      res.append("south")
    }
    if(x < arraySide - 1 && floor(y)(x+1) == 0){
      res.append("east")
    }
    return res
  }

  private def getRandomRoom(floor: Array[Array[Int]], numActualRoom: Int): Array[Int] = {
    val res: Array[Int] = Array(0, 0)
    var count: Int = 0
    val rdmNbre: Int = Random.nextInt(numActualRoom) + 1

    for(y: Int <- floor.indices){
      for(x: Int <- floor(0).indices){
        if(floor(y)(x) == 1){
          count += 1
        }

        if(rdmNbre == count){
          res(0) = x
          res(1) = y
          count += 1
        }
      }
    }
    return res
  }
}

object FloorTest extends App {
  val f: Floor = new Floor(15)
  f.generateFloor()
}
