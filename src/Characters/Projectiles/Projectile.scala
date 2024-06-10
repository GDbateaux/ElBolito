package Characters.Projectiles

import Characters.Hitbox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

abstract class Projectile(var damage: Int, var isFromHero: Boolean) extends DrawableObject {
  var speed: Int = 10
  var isFinish: Boolean = false
  var hitbox: Hitbox = _

  def addHitbox(): Unit

  def setSpeed(s: Int): Unit = {
    speed = s
  }

  def animate(): Unit

  override def draw(g: GdxGraphics): Unit
}
