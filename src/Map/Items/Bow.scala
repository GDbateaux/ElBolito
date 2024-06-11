package Map.Items
import Characters.Hero

class Bow extends Item {
  val text: String = "Vous avez reçu un arc"

  override def handle(h: Hero): Unit = {
    h.setWeaponType(h.WEAPON_TYPE_BOW)
  }
}
