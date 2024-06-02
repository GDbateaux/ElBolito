package Map


import Utils.Direction.Direction
import Utils.{Direction, Position}
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Floor(private val numRoom: Int) extends DrawableObject{
  private var arraySide: Int = 0
  private val PRODUCT_SIDE_ROOME: Double = 0.8
  private val tmpSide: Int = (numRoom * PRODUCT_SIDE_ROOME).toInt
  private var floorInt: Array[Array[Int]] = _
  private var floor: Array[Array[Room]] = _
  private var currentRoomPos: Position = Position(0,0)
  var currentRoom: Room = _
  var RoomDifficulty: Int = 1

  if(tmpSide % 2 == 0){
    arraySide = tmpSide + 1
  }
  else{
    arraySide = tmpSide
  }
  generateFloor()
  //displayRooms()

  def changeRoom(d: Direction): Unit = {
    var nextRoomPos: Position = Position(0, 0)

    if(d == Direction.NORTH){
      nextRoomPos = Position(currentRoomPos.x, currentRoomPos.y-1)
    }
    else if(d == Direction.SOUTH){
      nextRoomPos = Position(currentRoomPos.x, currentRoomPos.y+1)
    }
    else if (d == Direction.EAST) {
      nextRoomPos = Position(currentRoomPos.x - 1, currentRoomPos.y)
    }
    else if (d == Direction.WEST) {
      nextRoomPos = Position(currentRoomPos.x + 1, currentRoomPos.y)
    }

    if (nextRoomPos.x >= 0 && nextRoomPos.x < arraySide && nextRoomPos.y >= 0 && nextRoomPos.y < arraySide) {
      if (floor(nextRoomPos.y)(nextRoomPos.x) != null) {
        currentRoomPos = nextRoomPos
        currentRoom = floor(currentRoomPos.y)(currentRoomPos.x)
      }
    }
  }

  private def generateFloor(): Unit = {
    floorInt = generateBlueprint()
    addRoomType(floorInt)
    floor = generateFloorArray(floorInt)
    currentRoom = floor(currentRoomPos.y)(currentRoomPos.x)
  }

  def displayRooms(): Unit = {
    for (y: Int <- floorInt.indices) {
      for (x: Int <- floorInt(0).indices) {
        print(floorInt(y)(x))
      }
      println()
    }

    println()
    println()

    for (y: Int <- floor.indices) {
      for (x: Int <- floor(0).indices) {
        print(floor(y)(x))
      }
      println()
    }
  }

  private def generateFloorArray(f: Array[Array[Int]]): Array[Array[Room]] = {
    val res: Array[Array[Room]] = Array.ofDim(arraySide, arraySide)

    for (y: Int <- f.indices) {
      for (x: Int <- f(0).indices) {
        val neighborRooms: ArrayBuffer[Direction] = getNeighborDirections(f, Position(x,y))
        if(f(y)(x) == 1){
          res(y)(x) = new FightRoom(RoomDifficulty, neighborRooms)
        }
        else if(f(y)(x) == 2){
          currentRoomPos = Position(x,y)
          res(y)(x) = new StartRoom(neighborRooms)
        }
        else if(f(y)(x) == 3){
          res(y)(x) = new SpecialRoom(neighborRooms)
        }
        else if(f(y)(x) == 4){
          res(y)(x) = new BossRoom(RoomDifficulty, neighborRooms)
        }
      }
    }
    return res
  }

  private def generateBlueprint(): Array[Array[Int]] ={
    val floorInt: Array[Array[Int]] = Array.ofDim(arraySide, arraySide)
    var currentNumRoom: Int = 1
    var previousRoomX: Int = arraySide/2
    var previousRoomY: Int = arraySide/2
    var currentRoomX: Int = arraySide/2
    var currentRoomY: Int = arraySide/2
    val RANDOM_PERCENTAGE: Double = 0.25
    val PREVIOUS_PERCENTAGE: Double = 0.35
    floorInt(currentRoomY)(currentRoomX) = 1

    while(currentNumRoom < numRoom){
      val rdmNbre: Int = Random.nextInt(100)
      val availableRoom: ArrayBuffer[Position] = getFreeRoom(floorInt, Position(currentRoomX, currentRoomY))

      if(availableRoom.nonEmpty){
        val pos: Position = availableRoom(Random.nextInt(availableRoom.length))
        previousRoomX = currentRoomX
        previousRoomY = currentRoomY

        currentRoomX = pos.x
        currentRoomY = pos.y
        floorInt(currentRoomY)(currentRoomX) = 1

        currentNumRoom += 1
      }

      if(availableRoom.isEmpty){
        if (rdmNbre < ((PREVIOUS_PERCENTAGE / (PREVIOUS_PERCENTAGE + RANDOM_PERCENTAGE))*100).round.toInt) {
          currentRoomX = previousRoomX
          currentRoomY = previousRoomY
        }
        else {
          val rdmRoom: Position = getRandomRoom(floorInt, currentNumRoom)
          previousRoomX = currentRoomX
          previousRoomY = currentRoomY
          currentRoomX = rdmRoom.x
          currentRoomY = rdmRoom.y
        }
      }
      else{
        if (rdmNbre < PREVIOUS_PERCENTAGE * 100) {
          currentRoomX = previousRoomX
          currentRoomY = previousRoomY
        }
        else if(rdmNbre >= 100-RANDOM_PERCENTAGE*100) {
          val rdmRoom: Position = getRandomRoom(floorInt, currentNumRoom)
          previousRoomX = currentRoomX
          previousRoomY = currentRoomY
          currentRoomX = rdmRoom.x
          currentRoomY = rdmRoom.y
        }
      }
    }
    return floorInt
  }

  //0: No room; 1: Simple room; 2: Start room; 3: Alone room; 4: Boss room
  private def addRoomType(floor: Array[Array[Int]]): Unit = {
    val farthestRoom: ArrayBuffer[Position] = getFarthestPos(floor)
    val rdmNbre: Int = Random.nextInt(farthestRoom.length)
    val pos: Position = farthestRoom(rdmNbre)

    floor(arraySide/2)(arraySide/2) = 2
    for(y: Int <- floor.indices){
      for(x: Int <- floor(0).indices){
        if(floor(y)(x) == 1 && getFreeRoom(floor, Position(x,y)).length == 3){
          floor(y)(x) = 3
        }
      }
    }
    floor(pos.y)(pos.x) = 4
  }

  private def getFarthestPos(floor: Array[Array[Int]]): ArrayBuffer[Position] = {
    var currentPos: ArrayBuffer[Position] = new ArrayBuffer[Position]()
    var isFinish: Boolean = false
    val arr: Array[Array[Int]] = Array.ofDim(floor.length, floor(0).length)
    for(i: Int <- floor.indices){
      arr(i) = floor(i).clone()
    }
    currentPos.append(Position(arraySide/2, arraySide/2))

    while(!isFinish){
      val tmpPos: ArrayBuffer[Position] = new ArrayBuffer[Position]()

      for(p: Position <- currentPos){
        for(n <- getNeighbor(arr, p)){
          tmpPos.append(n)
        }
        arr(p.y)(p.x) = 0
      }
      if(tmpPos.isEmpty){
        isFinish = true
      }
      else{
        currentPos = tmpPos.distinct
      }
    }
    return currentPos
  }

  private def getNeighborDirections(floor: Array[Array[Int]], pos: Position): ArrayBuffer[Direction] = {
    val res: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()

    if (pos.y > 0 && floor(pos.y - 1)(pos.x) != 0) {
      res.append(Direction.NORTH)
    }
    if (pos.x > 0 && floor(pos.y)(pos.x - 1) != 0) {
      res.append(Direction.WEST)
    }
    if (pos.y < arraySide - 1 && floor(pos.y + 1)(pos.x) != 0) {
      res.append(Direction.SOUTH)
    }
    if (pos.x < arraySide - 1 && floor(pos.y)(pos.x + 1) != 0) {
      res.append(Direction.EAST)
    }
    return res
  }

  private def getNeighbor(floor: Array[Array[Int]], pos: Position): ArrayBuffer[Position] = {
    val res: ArrayBuffer[Position] = new ArrayBuffer[Position]()

    if (pos.y > 0 && floor(pos.y - 1)(pos.x) != 0) {
      res.append(Position(pos.x, pos.y-1))
    }
    if (pos.x > 0 && floor(pos.y)(pos.x - 1) != 0) {
      res.append(Position(pos.x-1, pos.y))
    }
    if (pos.y < arraySide - 1 && floor(pos.y + 1)(pos.x) != 0) {
      res.append(Position(pos.x, pos.y+1))
    }
    if (pos.x < arraySide - 1 && floor(pos.y)(pos.x + 1) != 0) {
      res.append(Position(pos.x+1, pos.y))
    }
    return res
  }

  private def getFreeRoom(floor: Array[Array[Int]], pos: Position): ArrayBuffer[Position] = {
    val res: ArrayBuffer[Position] = new ArrayBuffer[Position]()

    if(pos.y > 0 && floor(pos.y-1)(pos.x) == 0){
      res.append(Position(pos.x, pos.y-1))
    }
    if(pos.x > 0 && floor(pos.y)(pos.x-1) == 0){
      res.append(Position(pos.x-1, pos.y))
    }
    if(pos.y < arraySide - 1 && floor(pos.y+1)(pos.x) == 0){
      res.append(Position(pos.x, pos.y+1))
    }
    if(pos.x < arraySide - 1 && floor(pos.y)(pos.x+1) == 0){
      res.append(Position(pos.x+1, pos.y))
    }
    return res
  }

  private def getRandomRoom(floor: Array[Array[Int]], numActualRoom: Int): Position = {
    val res: Position = Position(0, 0)
    var count: Int = 0
    val rdmNbre: Int = Random.nextInt(numActualRoom) + 1

    for(y: Int <- floor.indices){
      for(x: Int <- floor(0).indices){
        if(floor(y)(x) == 1){
          count += 1
        }

        if(rdmNbre == count){
          res.x = x
          res.y = y
          count += 1
        }
      }
    }
    return res
  }

  override def draw(g: GdxGraphics): Unit = {
    val currentRoom: Room = this.currentRoom
    if(currentRoom != null){
      currentRoom.draw(g)
    }
  }
}

object FloorTest extends App {
  val f: Floor = new Floor(15)
}
