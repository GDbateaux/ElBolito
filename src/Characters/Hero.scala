package Characters

import Utils.Direction.Direction
import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject

class Hero() extends DrawableObject{
  private val SPRITE_WIDTH: Int = 32
  private val SPRITE_HEIGHT: Int = 32
  private val textureY: Int = 0

  private val NUM_FRAME_RUN: Int = 4
  private val currentFrame: Int = 0
  private val runSs: Spritesheet = new Spritesheet("data/images/lumberjack32.png", SPRITE_WIDTH, SPRITE_HEIGHT)

  def turn(d: Direction): Unit = {

  }

  override def draw(g: GdxGraphics): Unit = {
    g.draw(runSs.sprites(textureY)(currentFrame))
  }
}
