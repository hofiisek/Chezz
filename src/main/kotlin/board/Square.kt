package board

import piece.Piece

/**
 * A single square on the board. Is defined
 *
 * @author Dominik Hoftych
 */
data class Square(val position: Position, var piece: Piece? = null) {

    val rank: Int = 8 - position.row

    val file: Char = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')[position.col]

    val color: Char = if (1 > (position.row + position.col) % 2) 'W' else 'B'

    val text: String = "${file}${rank}"

    init {
        require(position.withinBoard) {
            "Square out of bounds"
        }
    }

    constructor(other: Square) : this(other.position, other.piece)
    constructor(other: Square, piece: Piece) : this(other.position, piece)

    override fun toString(): String {
        return "Square(text='$text')"
    }


}
