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
     * The square's position expressed in the algebraic notation
     */
    val an: String = "${position.file}${position.rank}"

    /**
     * Whether the square is occupied by some piece
     */
    val isOccupied: Boolean
        get() = piece != null

    /**
     * Whether the square is unoccupied
     */
    val isUnoccupied: Boolean
        get() = !isOccupied

    init {
        require(position.onBoard) {
            "Square out of bounds"
        }
    }

    /**
     * Initializes a new square occupied by given [piece] on the same position as the given [other] square
     */
    constructor(other: Square, piece: Piece?) : this(other.position, piece)

}

/**
 * Returns true if the square is occupied by player of the same color as given [piece]
 */
infix fun Square.occupiedBySamePlayerAs(piece: Piece): Boolean = this.piece?.player == piece.player

/**
 * Returns true if the square is occupied by the given [player]
 */
infix fun Square.occupiedBy(player: Player): Boolean = this.piece?.player == player