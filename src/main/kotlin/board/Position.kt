package board

import kotlin.math.abs

/**
 * Specific position on the chess board.
 *
 * @param row row index in range from 0 to 7
 * @param col column index in range from 0 to 7
 *
 * @author Dominik Hoftych
 */
data class Position(val row: Int, val col: Int) {

    /**
     * Rank (row) of this position in range from 1 to 8
     */
    val rank: Int by lazy {
        8 - row
    }

    /**
     * File (column) of this position, named from 'a' to 'h'
     */
    val file: Char by lazy {
        charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')[col]
    }

    /**
     * Whether the position is on the board
     */
    val onBoard: Boolean = row in (0..7) && col in (0..7)
}

infix fun Position.add(other: Position) = Position(this.row + other.row, this.col + other.col)
infix fun Position.add(other: Pair<Int, Int>) = this add Position(other.first, other.second)
infix fun Position.add(square: Square) = this add square.position

fun Position.add(x: Int, y: Int) = this add Position(x, y)

infix fun Position.diffCols(other: Position): Int = abs(this.col - other.col)