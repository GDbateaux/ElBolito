package Characters

import Utils.Vector2d
import ch.hevs.gdx2d.lib.GdxGraphics

trait Enemy {
  val hitbox: Hitbox
  var hp: Int

  def go(CoordinateCenter: Vector2d)
  def animate(elapsedTime: Double)

  def draw(g: GdxGraphics)
}
