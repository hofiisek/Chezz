package game

/**
 * A player, either black or white.
 *
 * @author Dominik Hoftych
 */
enum class Player {
    WHITE, BLACK
}

fun Player.color(): String = if(this == Player.WHITE) "w" else "b"