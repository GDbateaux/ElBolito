package Characters

import Utils.Direction.Direction
import Utils.{Direction, Vector2d}
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.Color

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Hitbox(private var centerPos: Vector2d, width: Double, height: Double) extends DrawableObject{
  var pos1: Vector2d = _
  var pos2: Vector2d = _
  var center: Vector2d = _
  updateCoordinates()

  def updateCenter(center: Vector2d): Unit = {
    centerPos = center
    updateCoordinates()
  }

  private def updateCoordinates(): Unit = {
    pos1 = new Vector2d((centerPos.x - width / 2).toFloat,
      (centerPos.y - height / 2).toFloat)
    pos2 = new Vector2d((centerPos.x + width / 2).toFloat,
      (centerPos.y + height / 2).toFloat)
    center = new Vector2d((pos1.x + pos2.x) / 2, (pos1.y + pos2.y) / 2)
  }
  override def draw(gdxGraphics: GdxGraphics): Unit = {
    updateCoordinates()
    gdxGraphics.setColor(Color.RED)
    gdxGraphics.drawFilledRectangle(centerPos.x, centerPos.y,
      width.toFloat, height.toFloat, 0)
  }

  def interect(h: Hitbox): Boolean = {
    val overlapX = h.pos1.x < pos2.x && h.pos2.x > pos1.x
    val overlapY = h.pos1.y < pos2.y && h.pos2.y > pos1.y

    if (overlapX && overlapY) {
      return true
    }
    return false
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
}
