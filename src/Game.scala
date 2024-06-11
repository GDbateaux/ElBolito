import Characters.Projectiles.{Projectile, ProjectileHandler}
import Characters.{Hero, Monster}
import Map.{BossRoom, Floor}
import Utils.Direction.{Direction, EAST, NULL}
import Utils.{Direction, Screen, Vector2d}
import ch.hevs.gdx2d.components.audio.SoundSample
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random


/**
 * ElBolito game in Scala
 *
 * @author Simon Masserey & Yolan Savioz
 * @version 1.0
 */
object Game {
  def main(args: Array[String]): Unit = {
    new Game(Screen.WIDTH, Screen.HEIGHT)
  }
}

class Game(windowWidth: Int, windowHeigth:Int) extends PortableApplication(windowWidth, windowHeigth) {
  private val ANIMATION_LENGTH_DAMAGE: Float = 1
  private val NUM_ROOM: Int = 10
  private val DRAW_HITBOX: Boolean = false
  private val SONG_VOLUME: Float = 0.5F
  private val SONG0_TIME: Float = 240
  private val SONG1_TIME: Float = 126
  private val SONG2_TIME: Float = 66
  private val ALPHA_CHANGE_TIME: Float = 0.05f
  private val INOX_SONG_TIME :Float = 6

  private var h: Hero = _
  private var f: Floor = _
  private val keyStatus: mutable.HashMap[Int, Boolean] = new mutable.HashMap[Int, Boolean]()
  private val buttonStatus: mutable.HashMap[Int, Boolean] = new mutable.HashMap[Int, Boolean]()
  private val pointerPos: Vector2d = new Vector2d(0, 0)
  private var currentTime: Float = 0
  private var mainMenu: Boolean = true
  private var invincibilityTime: Float = 0

  private var menuSong: SoundSample = _
  private var menuImage: BitmapImage = _
  private var menuText: BitmapImage = _
  private var alphaMenu: Float = 1
  private var addAlpha: Float = -ALPHA_CHANGE_TIME
  private var dt: Float = 0
  private var firstLoopGame: Boolean = true
  private var song0: SoundSample = _
  private var song1: SoundSample = _
  private var song2: SoundSample = _
  private var inoxSong: SoundSample = _
  private var bossSong: SoundSample = _
  private var isBossSong: Boolean = false
  private var currentSong: Int = Random.nextInt(3)
  private val songTime: ArrayBuffer[Float] = new ArrayBuffer[Float]()
  private val songs: ArrayBuffer[SoundSample] = new ArrayBuffer[SoundSample]()

  private val KEY_UP = Input.Keys.W
  private val KEY_RIGHT = Input.Keys.D
  private val KEY_DOWN = Input.Keys.S
  private val KEY_LEFT = Input.Keys.A
  private val KEY_SHIFT = Input.Keys.SHIFT_LEFT
  private val BUTTON_LEFT = Input.Buttons.LEFT
  private val BUTTON_RIGHT = Input.Buttons.RIGHT
  private val SPACE = Input.Keys.SPACE

  private var onlyOne: Boolean = true

  override def onInit(): Unit = {
    setTitle("El Bolito")

    menuSong = new SoundSample("data/sounds/mainMenu.mp3")
    menuImage = new BitmapImage("data/images/menu/image.png")
    menuText = new BitmapImage("data/images/menu/text.png")
    menuSong.setVolume(SONG_VOLUME)

    song0 = new SoundSample("data/sounds/song0.mp3")
    song1 = new SoundSample("data/sounds/song1.mp3")
    song2 = new SoundSample("data/sounds/song2.mp3")

    inoxSong = new SoundSample("data/sounds/inox.mp3")
    bossSong = new SoundSample("data/sounds/boss.mp3")

    songTime.append(SONG0_TIME)
    songTime.append(SONG1_TIME)
    songTime.append(SONG2_TIME)
    songs.append(song0)
    songs.append(song1)
    songs.append(song2)

    f = new Floor(NUM_ROOM, 0)
    h = new Hero(f.currentRoom.ROOM_CENTER, f.currentRoom.squareWidth)
    keyStatus(KEY_UP) = false
    keyStatus(KEY_RIGHT) = false
    keyStatus(KEY_DOWN) = false
    keyStatus(KEY_LEFT) = false
    keyStatus(KEY_SHIFT) = false
    keyStatus(SPACE) = false
    buttonStatus(BUTTON_LEFT) = false
    buttonStatus(BUTTON_RIGHT) = false

    menuSong.loop()
  }

  /**
   * This method is called periodically by the engine
   *
   * @param g
   */
  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()

    if(mainMenu){
      manageAlpha()
      g.drawPicture(Screen.WIDTH/2,Screen.HEIGHT/2, menuImage)
      g.drawAlphaPicture(Screen.WIDTH/2,Screen.HEIGHT/2, alphaMenu, menuText)
      manageMainMenu()
    }
    else{
      if(firstLoopGame){
        firstLoopGame = false
        menuSong.dispose()
        songs(currentSong).play()
      }
      manageSong()

      f.draw(g)
      f.currentRoom.manageRoom(h)
      f.currentRoom.doorAnimate(Gdx.graphics.getDeltaTime)

      manageHero()
      h.draw(g)

      ProjectileHandler.handle(g, f.currentRoom, h)

      if(DRAW_HITBOX){
        h.hitbox.draw(g)
        h.attackHitbox.draw(g)
        for(m <- f.currentRoom.monsters){
          m.hitbox.draw(g)
        }
      }

      g.drawFPS()
    }
  }

  private def manageAlpha(): Unit = {
    dt += Gdx.graphics.getDeltaTime

    if(dt >= ALPHA_CHANGE_TIME){
      dt -= ALPHA_CHANGE_TIME
      alphaMenu += addAlpha

      if(alphaMenu <= 0){
        addAlpha = -addAlpha
      }
      if(alphaMenu >= 1){
        addAlpha = -addAlpha
      }
    }
  }

  private def manageMainMenu(): Unit = {
    if (keyStatus(SPACE)) {
      mainMenu = false
    }
  }

  private def manageSong(): Unit = {
    currentTime += Gdx.graphics.getDeltaTime

    if(currentTime >= songTime(currentSong) && !f.currentRoom.isInstanceOf[BossRoom]){
      songs(currentSong).stop()
      currentTime = 0
      currentSong = (currentSong + 1) % 3
      songs(currentSong).play()
    }

    if (f.currentRoom.isInstanceOf[BossRoom]) {
      if(onlyOne){
        currentTime = 0
        onlyOne = false
        songs(currentSong).stop()
        inoxSong.play()
      }
      if(currentTime >= INOX_SONG_TIME && !isBossSong){
        inoxSong.stop()
        bossSong.loop()
        isBossSong = true
      }
    }
  }

  private def manageHero(): Unit = {
    var goDir: ArrayBuffer[Direction] = new ArrayBuffer[Direction]()
    val dirNoGo: ArrayBuffer[Direction] = f.currentRoom.wallContact(h.hitbox)
    val dirSwitchRoom: Direction = f.currentRoom.doorContact(h.hitbox)

    h.setWeaponType(h.WEAPON_TYPE_BOW)
    h.setWeaponType(h.WEAPON_TYPE_SWORD)

    if (keyStatus(KEY_UP)) {
      h.turn(Direction.NORTH)
      goDir.append(Direction.NORTH)
    }
    if (keyStatus(KEY_DOWN)) {
      h.turn(Direction.SOUTH)
      goDir.append(Direction.SOUTH)
    }
    if (keyStatus(KEY_RIGHT)) {
      h.turn(Direction.EAST)
      goDir.append(Direction.EAST)
    }
    if (keyStatus(KEY_LEFT)) {
      h.turn(Direction.WEST)
      goDir.append(Direction.WEST)
    }
    if(buttonStatus(BUTTON_RIGHT)) {
      buttonStatus(BUTTON_RIGHT) = false
    }
    if (buttonStatus(BUTTON_LEFT)) {
      h.attack(pointerPos)
      buttonStatus(BUTTON_LEFT) = false
    }

    if (dirSwitchRoom != Direction.NULL && goDir.contains(dirSwitchRoom) && f.currentRoom.isClean) {
      if(dirSwitchRoom == Direction.SOUTH) {
        h.position.x = f.currentRoom.ROOM_NORTH.x
        h.position.y = f.currentRoom.ROOM_NORTH.y
      }
      else if (dirSwitchRoom == Direction.NORTH) {
        h.position.x = f.currentRoom.ROOM_SOUTH.x
        h.position.y = f.currentRoom.ROOM_SOUTH.y
      }
      else if (dirSwitchRoom == Direction.EAST) {
        h.position.x = f.currentRoom.ROOM_WEST.x
        h.position.y = f.currentRoom.ROOM_WEST.y
      }
      else if (dirSwitchRoom == Direction.WEST) {
        h.position.x = f.currentRoom.ROOM_EAST.x
        h.position.y = f.currentRoom.ROOM_EAST.y
      }
      f.changeRoom(dirSwitchRoom)
    }

    goDir = goDir.diff(dirNoGo)

    h.go(goDir)

    if(keyStatus(KEY_SHIFT)) {
      h.roll()
    }


    if(!keyStatus(KEY_UP) && !keyStatus(KEY_DOWN) &&
      !keyStatus(KEY_LEFT) && !keyStatus(KEY_RIGHT)){
      h.setMove(false)
    }

    if(invincibilityTime >= h.INVINCIBILITY_TIME){
      invincibilityTime = 0
      h.setInvisibility(false)
    }

    if(h.isInvincible){
      invincibilityTime += Gdx.graphics.getDeltaTime
    }

    if(h.hp <= 0) {
      h.setSpeed(0)
    }

    h.animate(Gdx.graphics.getDeltaTime)
  }

  override def onKeyUp(keyCode: Int): Unit = {
    super.onKeyUp(keyCode)
    keyStatus(keyCode) = false
  }

  override def onKeyDown(keyCode: Int): Unit = {
    super.onKeyDown(keyCode)
    keyStatus(keyCode) = true
  }

  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)
    pointerPos.x = x
    pointerPos.y = y
    buttonStatus(button) = true
  }
}