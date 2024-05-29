package Characters

import Utils.Coordinate
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.Color

import scala.util.Random

class Hitbox(charPos: Coordinate, relativeCenterPos: Coordinate, width: Double, height: Double) extends DrawableObject{
  var pos1: Coordinate = _
  var pos2: Coordinate = _
  var center: Coordinate = _
  updateCoordinates()

  private def updateCoordinates(): Unit = {
    pos1 = Coordinate((charPos.x + relativeCenterPos.x - width / 2).toFloat,
      (charPos.y + relativeCenterPos.y - height / 2).toFloat)
    pos2 = Coordinate((charPos.x + relativeCenterPos.x + width / 2).toFloat,
      (charPos.y + relativeCenterPos.y + height / 2).toFloat)
    center = Coordinate((pos1.x + pos2.x) / 2, (pos1.y + pos2.y) / 2)
  }
  override def draw(gdxGraphics: GdxGraphics): Unit = {
    updateCoordinates()

    gdxGraphics.setColor(Color.RED)
    gdxGraphics.drawRectangle(charPos.x + relativeCenterPos.x, charPos.y + relativeCenterPos.y,
      width.toFloat, height.toFloat, 0)
  }

  def interect(h: Hitbox): Boolean = {
    val overlapX = h.pos1.x < pos2.x && h.pos2.x > pos1.x
    val overlapY = h.pos1.y < pos2.y && h.pos2.y > pos1.y

    if (overlapX && overlapY) {
      println(Random.nextInt(1000))
      return true
    }
    return false
  }
}
