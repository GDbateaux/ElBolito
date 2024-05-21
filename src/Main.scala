import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics


/**
 * Hello World demo in Scala
 *
 * @author Pierre-André Mudry (mui)
 * @version 1.0
 */
object Main {

  def main(args: Array[String]): Unit = {
    new Main
  }
}

class Main extends PortableApplication {

  override def onInit(): Unit = {
    setTitle("El Bolito")
  }

  /**
   * This method is called periodically by the engine
   *
   * @param g
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Clears the screen
    g.clear()

    // Draw everything
    g.drawFPS()
    g.drawSchoolLogo()
  }
}
