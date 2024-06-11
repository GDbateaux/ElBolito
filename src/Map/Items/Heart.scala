package Map.Items

import Characters.Hero
import ch.hevs.gdx2d.components.bitmaps.Spritesheet

class Heart extends Item {
  val text: String = "Vous vous êtes soigné"
  def handle(h: Hero): Unit = {
    if(h.hp < h.MAX_HEALTH){
      h.hp += 1
    }
  }
}
