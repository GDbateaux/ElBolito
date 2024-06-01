package Map

import Characters.Hitbox
import Utils.Vector2d

class Obstacle(position: Vector2d, width: Float) {
  val hitbox: Hitbox = new Hitbox(new Vector2d(position.x + width/2, position.y + width/2), width, width)
}
