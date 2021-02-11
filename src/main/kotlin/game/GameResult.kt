package game

/**
 * The result of the game - either draw or the [winningPlayer] wins.
 *
 * @author Dominik Hoftych
 */
sealed class GameResult(open val winningPlayer: Player? = null) {

    /**
     * The [winningPlayer] checkmated the opponent
     */
    data class Checkmate(override val winningPlayer: Player?) : GameResult(winningPlayer)

    /**
     * The game ends as a draw
     */
    object Stalemate : GameResult(null)

    /**
     * The [winningPlayer] wins due to the opponent having no time left
     */
    data class WinOnTime(override val winningPlayer: Player?) : GameResult(winningPlayer)

    /**
     * The game ends as a draw due to the threefold repetition rule
     */
    object ThreefoldRepetition : GameResult(null)

    /**
     * The game ends as a draw due to the fifty-move rule
     */
    object FiftyMoveRule : GameResult(null)
}