package Characters.Projectiles

import Characters.{Enemy, Hero}
import Map.{Obstacle, Room}
import ch.hevs.gdx2d.lib.GdxGraphics

import scala.collection.mutable.ArrayBuffer

object ProjectileHandler {
  val projectiles: ArrayBuffer[Projectile] = new ArrayBuffer[Projectile]()

  def handle(g: GdxGraphics, currentRoom: Room, h: Hero): Unit = {
    var idx: Int = 0

    while (idx < projectiles.length) {
      val p: Projectile = projectiles(idx)
      if (p.isFinish) {
        projectiles.subtractOne(p)
      }
      else{
        p.animate()
        p.draw(g)
        for(o: Obstacle <- currentRoom.roomObstacles){
          if(o.hitbox.intersect(p.hitbox)){
            p.isFinish = true
          }
        }
        if(p.isFromHero){
          for (m: Enemy <- currentRoom.monsters) {
            if (m.hitbox.intersect(p.hitbox)) {
              m.hp -= p.damage
              m.invincibleFrameRemain = m.INVINCIBLE_FRAME
              p.isFinish = true
            }
          }
        }
        else{
          if(h.hitbox.intersect(p.hitbox)){
            h.hp -= p.damage
            p.isFinish = true
          }
        }

        idx += 1
      }
    }
  }
}
