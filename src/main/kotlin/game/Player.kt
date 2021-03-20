package game

/**
 * A player, either black or white.
 *
 * @author Dominik Hoftych
 */
enum class Player {
    WHITE, BLACK
}

/**
 * The opposite player
 */
val Player.theOtherPlayer: Player
    get() = if (this == Player.WHITE) Player.BLACK else Player.WHITE

/**
 * Color of the player, either "w" as white, or "b" as black
 */
fun Player.color(): String = if (this == Player.WHITE) "w" else "b"
