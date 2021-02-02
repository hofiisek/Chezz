package board

import game.Player
import piece.Piece

/**
 * A single square on the board which may or may not be occupied by a piece.
 *
 * @param position position on the board
 * @param piece piece occupying this square, may be null
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
     * Initializes a new square on the same position as the given [other] square
     */
    constructor(other: Square, piece: Piece?) : this(other.position, piece)

}

/**
 * Returns true if the receiver [Square] is occupied by player of the same color as given [piece]
 */
infix fun Square.occupiedBySamePlayerAs(piece: Piece): Boolean = this.piece?.player == piece.player

/**
 * Returns true if the receiver [Square] is occupied by the given [player]
 */
infix fun Square.occupiedBy(player: Player): Boolean = this.piece?.player == player