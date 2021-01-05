package board

import piece.Shift

data class Position(val row: Int, val col: Int) {
    val withinBoard: Boolean = row in (0..7) && col in (0..7)
}

infix fun Position.add(shift: Shift) = Position(this.row + shift.first, this.col + shift.second)
infix fun Position.add(other: Position) = Position(this.row + other.row, this.col + other.col)
