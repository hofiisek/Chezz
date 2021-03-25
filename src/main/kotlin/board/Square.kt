package board

import game.Player
import piece.Piece

/**
 * A square on a specific [position] on the board which may or may not be occupied by a [piece].
 *
 * @author Dominik Hoftych
 */
data class Square(val position: Position, val piece: Piece? = null) {

    /**
     * Whether the square is unoccupied
     */
    val isUnoccupied: Boolean get() = piece == null

    /**
     * Whether the square is occupied
     */
    val isOccupied: Boolean get() = piece != null

    init {
        require(position.isOnBoard) {
            "Square out of bounds"
        }
    }
}

/**
 * Returns true if the square is occupied by the given [player]
 */
infix fun Square.isOccupiedBy(player: Player): Boolean = this.piece?.player == player
