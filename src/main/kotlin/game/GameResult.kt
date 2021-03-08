package game

import game.GameResult.*

/**
 * The result of the game, either a win, draw, or unknown if the game is still being played.
 *
 * @author Dominik Hoftych
 */
sealed class GameResult {
    data class WhiteWins(val type: WinType) : GameResult()
    data class BlackWins(val type: WinType) : GameResult()
    data class Draw(val type: DrawType) : GameResult()
    object StillPlaying : GameResult()
}

/**
 * Returns a PGN string representing the game result
 */
fun GameResult.asString(): String = when (this) {
    is Draw -> "1/2-1/2"
    is WhiteWins -> "1-0"
    is BlackWins -> "0-1"
    StillPlaying -> "*"
}


/**
 * Possible ways of winning
 */
enum class WinType {
    CHECKMATE,
    TIMEOUT
}

/**
 * Possible ways of a draw
 */
enum class DrawType {
    STALEMATE,
    THREEFOLD_REPETITION,
    FIFTY_MOVE_RULE
}