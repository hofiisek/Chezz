package board

import game.Player
import piece.Piece

/**
 * A single square on the board. May or may not be occupied by a piece.
 *
 * @param position [Position] on the board as a pair of x and y coordinates
 * @param piece piece occupying this square, may be null
 *
 * @author Dominik Hoftych
 */
data class Square(val position: Position, val piece: Piece? = null) {

    /**
     * Rank (row) of this square in range from 1 to 8
     */
    val rank: Int = 8 - position.row

    /**
     * File (column) of this square, named from 'a' to 'h'
     */
    val file: Char = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')[position.col]

    /**
     * Text representation of the position (e.g. h8, c5, ..)
     */
    val text: String = "${file}${rank}"


    /**
     * Whether the square is occupied by some piece
     */
    val isOccupied: Boolean
        get() = piece != null

    init {
        require(position.onBoard) {
            "Square out of bounds"
        }
    }

    constructor(row: Int, col: Int, piece: Piece? = null) : this(Position(row,col), piece)
    constructor(other: Square) : this(other.position, other.piece)
    constructor(other: Square, piece: Piece?) : this(other.position, piece)

    override fun toString(): String {
        return "Square(position='$position', text=$text, piece=${piece?.name ?: "None"})"
    }

}

/**
 * Returns true if the receiver [Square] is occupied by player of the same color as given [piece]
 */
infix fun Square.occupiedBySamePlayerAs(piece: Piece): Boolean = this.piece?.player == piece.player

/**
 * Returns true if the receiver [Square] is occupied by the given [player]
 */
infix fun Square.occupiedBy(player: Player): Boolean = this.piece?.player == player