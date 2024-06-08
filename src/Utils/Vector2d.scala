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

  def length(): Double = {
    return math.sqrt(math.pow(x, 2) + math.pow(y, 2))
  }

  def normalize(): Vector2d = {
    val length: Double = this.length()
    if(length != 0){
      return new Vector2d((x/length).toFloat, (y/length).toFloat)
    }
    return new Vector2d(0,0)
  }
}
