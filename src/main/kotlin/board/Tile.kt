package board

import piece.Piece

/**
 * @author Dominik Hoftych
 */
data class Tile(val row: Int, val col: Int, var piece: Piece? = null) {

    val rank: Int = 8 - row

    val file: Char = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')[col]

    init {
        require(row in 0..7 && col in 0..7) {
            "Tile out of bounds"
        }
    }

    constructor(other: Tile) : this(other.row, other.col, other.piece)
    constructor(other: Tile, piece: Piece) : this(other.row, other.col, piece)
}
