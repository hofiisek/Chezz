package piece

import board.Square
import game.Move
import game.Player

/**
 * Movement of pieces defined as a pair of row and column shift (in this order)
 */
typealias Shift = Pair<Int, Int>

/**
 * Abstract parent of all chess pieces, i.e. pawn, rook, bishop, knight, queen and king.
 * Resides on a particular [Square], belongs to one of the [Player]s and its movement is defined by a set of [Shift]s.
 */
sealed class Piece {

    abstract val position: Square
    abstract val player: Player
    val moveHistory: MutableList<Move> = mutableListOf()
    
    protected abstract val shifts: Set<Shift>

    fun unicode(): String = when(this) {
        is Pawn -> if (player == Player.WHITE) "\u2659" else "\u265F"
        is Rook -> if (player == Player.WHITE) "\u2656" else "\u265C"
        is Knight -> if (player == Player.WHITE) "\u2658" else "\u265E"
        is Bishop -> if (player == Player.WHITE) "\u2657" else "\u265D"
        is Queen -> if (player == Player.WHITE) "\u2655" else "\u265B"
        is King -> if (player == Player.WHITE) "\u2654" else "\u265A"
    }
}

data class Pawn(override val player: Player, override val position: Square) : Piece() {
    
   override val shifts = setOf(
        Shift(+1, 0),
        Shift(+1, -1),
        Shift(+1, +1)
    )

}

data class Rook(override val player: Player, override val position: Square) : Piece() {

    override val shifts = (1..7).map {
        setOf(
            Shift(-it, 0), // up
            Shift(0, it),  // right
            Shift(it, 0),  // down
            Shift(0, -it)  // left
        )
    }.flatten().toSet()

}

data class Knight(override val player: Player, override val position: Square) : Piece() {

    override val shifts = setOf(
            Shift(-2, 1),  // up->right
            Shift(-1, 2),  // right->up
            Shift(1, 2),   // right->down
            Shift(2, 1),   // down->right
            Shift(2, -1),  // down->left
            Shift(1, -2),  // left->down
            Shift(-1, -2), // left->up
            Shift(-2, -1), // up->left
    )

}

data class Bishop(override val player: Player, override val position: Square) : Piece() {

    override val shifts = (1..7).map {
        setOf(
            Shift(-it, it), // up-right
            Shift(it, it),  // down-right
            Shift(it, -it),  // down-left
            Shift(-it, -it)  // up-left
        )
    }.flatten().toSet()

}

data class Queen(override val player: Player, override val position: Square) : Piece() {

    override val shifts = (1..7).map {
        setOf(
            // clockwise
            Shift(-it, 0),  // up
            Shift(-it, it),  // up-right
            Shift(0, it), // right
            Shift(it, it),  // down-right
            Shift(it, 0),  // down
            Shift(it, -it), // down-left
            Shift(0, -it),  // left
            Shift(-it, -it)  // up-left
        )
    }.flatten().toSet()

}

data class King(override val player: Player, override val position: Square) : Piece() {

    override val shifts = setOf(
            // clockwise
            Shift(-1, 0),  // up
            Shift(-1, 1),  // up-right
            Shift(0, 1), // right
            Shift(1, 1),  // down-right
            Shift(1, 0),  // down
            Shift(1, -1), // down-left
            Shift(0, -1),  // left
            Shift(-1, -1)  // up-left
    )

}
