package Utils

import scala.collection.mutable.ArrayBuffer

case class Cell(position: Position, var g: Int, h: Int, var parent: Cell = null) {
  def f: Int = g + h
}

class AStar(grid: Array[Array[Int]]) {
  val openList: ArrayBuffer[Cell] = new ArrayBuffer[Cell]();
  val closedList: ArrayBuffer[Cell] = new ArrayBuffer[Cell]();

  def findPath(start: Position, end: Position): ArrayBuffer[Position] = {
    openList += Cell(start, 0, heuristic(start, end))

    while (openList.nonEmpty) {
      val current = openList.minBy(_.f)
      openList -= current
      closedList += current

      if (current.position == end) {
        return reconstructPath(current, end)
      }

      for (neighbor <- getNeighbors(current, end)) {
        if (!closedList.exists(cell => cell.position == neighbor.position)) {
          if (!openList.exists(cell => cell.position == neighbor.position)) {
            val newCell = Cell(neighbor.position, current.g + 1, heuristic(neighbor.position, end), current)
            openList += newCell
          } else if (neighbor.g < current.g) {
            openList.find(cell => cell.position == neighbor.position).foreach(cell => {
              cell.g = neighbor.g
              cell.parent = current
            })
          }
        }
      }
    }

    return new ArrayBuffer[Position]();
  }

  def getNeighbors(cell: Cell, end: Position): ArrayBuffer[Cell] = {
    val directions = List((-1, 0), (1, 0), (0, -1), (0, 1)) // Up, Down, Left, Right
    val neighbors = new ArrayBuffer[Cell]()

    for ((dx, dy) <- directions) {
      val newPosition = Position(cell.position.x + dx, cell.position.y + dy)

      if (isValid(newPosition)) {
        val newCell = Cell(newPosition, cell.g + 1, heuristic(newPosition, end))
        neighbors += newCell
      }
    }

    neighbors
  }


  def isValid(position: Position): Boolean = {
    val x = position.x.toInt
    val y = position.y.toInt
    println("Astar" + x + " " + y)
    if(x >= 0 && y >= 0 && x < grid(0).length && y < grid.length) {
      println(grid(y)(x))
    }
    x >= 0 && y >= 0 && x < grid(0).length && y < grid.length && grid(y)(x) != 3
  }

  def heuristic(start: Position, end: Position): Int = {
    Math.abs(start.x - end.x).toInt + Math.abs(start.y - end.y).toInt
  }

  def reconstructPath(cell: Cell, end: Position): ArrayBuffer[Position] = {
    var path: ArrayBuffer[Position] = new ArrayBuffer[Position]();
    var current = cell

    while (current != null) {
      path.prepend(current.position)
      current = current.parent
    }

    path
  }
}
