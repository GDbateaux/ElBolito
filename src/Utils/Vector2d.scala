package Utils

class Vector2d(var x: Float, var y: Float){
  def add(v: Vector2d): Vector2d = {
    return new Vector2d(this.x + v.x, this.y + v.y)
  }

  def sub(v: Vector2d): Vector2d = {
    return new Vector2d(this.x - v.x, this.y - v.y)
  }
}
