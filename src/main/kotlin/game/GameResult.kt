package game

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