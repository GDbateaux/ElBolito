package Characters

import Utils.Vector2d
import ch.hevs.gdx2d.lib.GdxGraphics

trait Enemy {
  val hitbox: Hitbox
  var hp: Int
  val INVINCIBLE_FRAME: Int = 2
  var invincibleFrameRemain: Int = 0
  protected var invincibleTransparence: Boolean = false

  def go(CoordinateCenter: Vector2d)
  def animate(elapsedTime: Double)

  def draw(g: GdxGraphics)
}
