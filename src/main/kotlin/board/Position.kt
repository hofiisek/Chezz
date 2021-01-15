package board

import javafx.scene.paint.Color


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
     * Whether the position is on the board
     */
    val onBoard: Boolean = row in (0..7) && col in (0..7)
}

infix fun Position.add(other: Position) = Position(this.row + other.row, this.col + other.col)
infix fun Position.add(other: Pair<Int, Int>) = this add Position(other.first, other.second)
infix fun Position.add(square: Square) = this add square.position
