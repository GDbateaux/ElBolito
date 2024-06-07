package Utils

import com.badlogic.gdx.math.Vector2

class Vector2d(var x: Float, var y: Float){
  def add(v: Vector2d): Vector2d = {
    return new Vector2d(this.x + v.x, this.y + v.y)
  }

  def sub(v: Vector2d): Vector2d = {
    return new Vector2d(this.x - v.x, this.y - v.y)
  }

  def mult(l: Float): Vector2d = {
    return new Vector2d(x * l, y * l)
  }

  def normalize(): Vector2d = {
    val length: Double = math.sqrt(math.pow(x, 2) + math.pow(y, 2))

    return new Vector2d((x/length).toFloat, (y/length).toFloat)
  }
}
