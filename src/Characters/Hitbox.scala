package Characters

import Utils.Direction.Direction
import Utils.{Direction, Vector2d}
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.Color

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Hitbox(private var centerPos: Vector2d, width: Double, height: Double, private var angle: Double = 0.0) extends DrawableObject {
  var pos1: Vector2d = _
  var pos2: Vector2d = _
  var pos3: Vector2d = _
  var pos4: Vector2d = _
  var center: Vector2d = _
  updateCoordinates()

  def updateCenter(center: Vector2d): Unit = {
    centerPos = center
    updateCoordinates()
  }

  def neighborDirection(h: Hitbox): Direction = {
    val verticalDif = h.center.y - center.y
    val horizontalDif = h.center.x - center.x

    if (math.abs(verticalDif) > math.abs(horizontalDif)) {
      if (verticalDif > 0) Direction.NORTH else Direction.SOUTH
    } else {
      if (horizontalDif > 0) Direction.EAST else Direction.WEST
    }
  }
  def updateAngle(newAngle: Double): Unit = {
    angle = newAngle
    updateCoordinates()
  }

  private def updateCoordinates(): Unit = {
    // Calculate the half widths
    val halfWidth = width / 2
    val halfHeight = height / 2

    // Calculate the rotated corner positions
    val rad = Math.toRadians(angle)
    val cos = Math.cos(rad)
    val sin = Math.sin(rad)

    // Top-left corner
    val x1 = centerPos.x - halfWidth * cos + halfHeight * sin
    val y1 = centerPos.y - halfWidth * sin - halfHeight * cos

    // Top-right corner
    val x2 = centerPos.x + halfWidth * cos + halfHeight * sin
    val y2 = centerPos.y + halfWidth * sin - halfHeight * cos

    // Bottom-right corner
    val x3 = centerPos.x + halfWidth * cos - halfHeight * sin
    val y3 = centerPos.y + halfWidth * sin + halfHeight * cos

    // Bottom-left corner
    val x4 = centerPos.x - halfWidth * cos - halfHeight * sin
    val y4 = centerPos.y - halfWidth * sin + halfHeight * cos

    // Set the position vectors
    pos1 = new Vector2d(x1.toFloat, y1.toFloat)
    pos2 = new Vector2d(x2.toFloat, y2.toFloat)
    pos3 = new Vector2d(x3.toFloat, y3.toFloat)
    pos4 = new Vector2d(x4.toFloat, y4.toFloat)
    center = new Vector2d(centerPos.x.toFloat, centerPos.y.toFloat)
  }

  override def draw(gdxGraphics: GdxGraphics): Unit = {
    updateCoordinates()
    gdxGraphics.setColor(Color.RED)
    gdxGraphics.drawFilledRectangle(centerPos.x, centerPos.y, width.toFloat, height.toFloat, angle.toFloat)
  }

  def intersect(h: Hitbox): Boolean = {
    // Separating Axis Theorem (SAT) for rotated rectangles
    val axes = Array(
      getEdgeVector(pos1, pos2), // edge 1
      getEdgeVector(pos2, pos3), // edge 2
      getEdgeVector(h.pos1, h.pos2), // h edge 1
      getEdgeVector(h.pos2, h.pos3)  // h edge 2
    )

    !axes.exists(axis => {
      val projectionA = project(axis, Array(pos1, pos2, pos3, pos4))
      val projectionB = project(axis, Array(h.pos1, h.pos2, h.pos3, h.pos4))
      projectionA._2 < projectionB._1 || projectionB._2 < projectionA._1
    })
  }

  private def getEdgeVector(p1: Vector2d, p2: Vector2d): Vector2d = {
    new Vector2d(p2.x - p1.x, p2.y - p1.y)
  }

  private def project(axis: Vector2d, vertices: Array[Vector2d]): (Float, Float) = {
    val projections = vertices.map(v => (v.x * axis.x + v.y * axis.y) / axis.length())
    (projections.min.toFloat, projections.max.toFloat)
  }
}