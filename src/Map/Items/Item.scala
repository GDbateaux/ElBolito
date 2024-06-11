package Map.Items

import Characters.Hero
import ch.hevs.gdx2d.components.bitmaps.Spritesheet

trait Item {
  val text: String

  def handle(h: Hero): Unit
}
