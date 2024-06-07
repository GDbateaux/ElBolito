package Utils

import scala.collection.mutable.ArrayBuffer

case class Cell(position: Vector2d, g: Int, h: Int) {
  def f: Int = g + h
}

class AStar(grid: Array[Array[Int]]) {
  val openList: ArrayBuffer[Cell] = new ArrayBuffer[Cell]();
  val closedList: ArrayBuffer[Cell] = new ArrayBuffer[Cell]();

  def findPath(start: Vector2d, end: Vector2d): List[Vector2d] = {
    openList += Cell(start, 0, heuristic(start, end))

    while (openList.nonEmpty) {
      val current = openList.minBy(_.f)
      openList -= current
      closedList += current

      if (current.position == end) {
        return reconstructPath(current)
      }

      for (neighbor <- getNeighbors(current)) {
        if (!closedList.exists(cell => cell.position == neighbor.position)) {
          if (!openList.exists(cell => cell.position == neighbor.position)) {
            openList += neighbor
          } else if (neighbor.g < current.g) {
            openList.find(cell => cell.position == neighbor.position).foreach(cell => cell.g = neighbor.g)
          }
        }
      }
    }

    List.empty
  }

  def getNeighbors(cell: Cell): List[Cell] = {
    val neighbors = List(
      Cell(cell.position.add(new Vector2d(-1, 0)), cell.g + 1, 0),
      Cell(cell.position.add(new Vector2d(1, 0)), cell.g + 1, 0),
      Cell(cell.position.add(new Vector2d(0, -1)), cell.g + 1, 0),
      Cell(cell.position.add(new Vector2d(0, 1)), cell.g + 1, 0)
    )

    neighbors.filter(neighbor => isValid(neighbor.position))
  }

  def isValid(position: Vector2d): Boolean = {
    val x = position.x.toInt
    val y = position.y.toInt
    x >= 0 && y >= 0 && x < grid.length && y < grid(0).length && grid(x)(y) == 0
  }

  def heuristic(start: Vector2d, end: Vector2d): Int = {
    Math.abs(start.x - end.x).toInt + Math.abs(start.y - end.y).toInt
  }

  def reconstructPath(cell: Cell): ArrayBuffer[Vector2d] = {
    var path: ArrayBuffer[Vector2d] = new ArrayBuffer[Vector2d]();
    var current = cell

    while (current != null) {
      path.prepend(current.position)
      current = closedList.find(cell => getNeighbors(cell).exists(neighbor => neighbor.position == current.position)).orNull
    }

    path
  }
}
