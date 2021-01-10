package game

/**
 * A player, either black or white.
 *
 * @author Dominik Hoftych
 */
enum class Player {
    WHITE, BLACK
}

fun Player.text(): String = if(this == Player.WHITE) "w" else "b"