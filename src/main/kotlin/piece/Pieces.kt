package piece

import board.Board
import board.Position
import board.Square
import game.*

/**
 * Abstract parent of all chess pieces, i.e. pawn, rook, bishop, knight, queen and king.
 * Each piece belongs to a particular [player], occupies a particular square on the [position] and
 * records its [history], i.e. a list of previous its previous positions.
 * 
 * @author Dominik Hoftych
 */
sealed class Piece {

    /**
     * The player to whom the piece belongs
     */
    abstract val player: Player

    /**
     * The position of the piece on the board
     */
    abstract val position: Position

    /**
     * Ordered list of positions that were occupied by the piece previously
     */
    abstract val history: List<Position>
    
    /**
     * Name of the piece in the "pieceType_colorLetter" format, e.g. Rook_b or King_w.
     * Is used when loading piece images so it MUST correspond.
     */
    abstract val name: String

    /**
     * Piece expressed in the commonly used algebraic notation
     */
    abstract val an: String

    /**
     * Whether the piece has already moved from its original position
     */
    val hasMoved: Boolean
        get() = history.isNotEmpty()

    /**
     * The opposite player
     */
    val theOtherPlayer: Player
        get() = if (player == Player.WHITE) Player.BLACK else Player.WHITE

    /**
     * Movement of the piece defined as a set of directions along x and y axis respectively.
     * Defaults to an empty set but is overridden by each piece type except for pawn, whose movement
     * is a bit more complex.
     */
    open val movement: Set<Direction> = emptySet()

    /**
     * Returns the set of allowed moves w.r.t. given [board].
     * If [validateForCheck] is false, the resulting list may contain moves that
     * would put or leave the king in check, which is not allowed in the game.
     * However, such moves are still considered check moves,
     * see https://www.fide.com/FIDE/handbook/LawsOfChess.pdf, paragraph 3.1.
     */
    fun getAllowedMoves(board: Board, validateForCheck: Boolean = true): Set<Move> =
        MoveGenerator.generate(this, board, validateForCheck)

}

/**
 * Moves with the piece to given [square].
 * A new instance of [Piece] is initialized on its new position with the move recorded in its history list.
 */
infix fun Piece.moveTo(square: Square): Piece = when(this) {
    is Pawn -> Pawn(player, square.position, history.plus(square.position))
    is Rook -> Rook(player, square.position, history.plus(square.position))
    is Knight -> Knight(player, square.position, history.plus(square.position))
    is Bishop -> Bishop(player, square.position, history.plus(square.position))
    is Queen -> Queen(player, square.position, history.plus(square.position))
    is King -> King(player, square.position, history.plus(square.position))
}

data class Pawn(
        override val player: Player,
        override val position: Position,
        override val history: List<Position> = listOf()
) : Piece() {
    
    override val name: String = "Pawn_${player.color()}"
    override val an: String = "P"
}

data class Rook(
        override val player: Player,
        override val position: Position,
        override val history: List<Position> = listOf()
) : Piece() {
    
    override val name: String = "Rook_${player.color()}"
    override val an: String = "R"
    override val movement: Set<Direction> = setOf(
            Direction(-1, 0), // up
            Direction(0, 1),  // right
            Direction(1, 0),  // down
            Direction(0, -1)  // left
    )
}

data class Knight(
        override val player: Player,
        override val position: Position,
        override val history: List<Position> = listOf()
) : Piece() {
    
    override val name: String = "Knight_${player.color()}"
    override val an: String = "N"
    override val movement: Set<Direction> = setOf(
            Direction(-2, 1),  // up->right
            Direction(-1, 2),  // right->up
            Direction(1, 2),   // right->down
            Direction(2, 1),   // down->right
            Direction(2, -1),  // down->left
            Direction(1, -2),  // left->down
            Direction(-1, -2), // left->up
            Direction(-2, -1), // up->left
    )
}

data class Bishop(
        override val player: Player,
        override val position: Position,
        override val history: List<Position> = listOf()
) : Piece() {
    
    override val name: String = "Bishop_${player.color()}"
    override val an: String = "B"
    override val movement: Set<Direction> = setOf(
            Direction(-1,  1), // up-right
            Direction(1,  1),  // down-right
            Direction(1, - 1),  // down-left
            Direction(-1, - 1)  // up-left
    )
}

data class Queen(
        override val player: Player,
        override val position: Position,
        override val history: List<Position> = listOf()
) : Piece() {
    
    override val name: String = "Queen_${player.color()}"
    override val an: String = "Q"
    override val movement: Set<Direction> = setOf(
            Direction(-1, 0),  // up
            Direction(-1, 1),  // up-right
            Direction(0, 1), // right
            Direction(1, 1),  // down-right
            Direction(1, 0),  // down
            Direction(1, -1), // down-left
            Direction(0, -1),  // left
            Direction(-1, -1)  // up-left
    )
}

data class King(
        override val player: Player,
        override val position: Position,
        override val history: List<Position> = listOf()
) : Piece() {
    
    override val name: String = "King_${player.color()}"
    override val an: String = "K"
    override val movement: Set<Direction> = setOf(
            Direction(-1, 0),  // up
            Direction(-1, 1),  // up-right
            Direction(0, 1), // right
            Direction(1, 1),  // down-right
            Direction(1, 0),  // down
            Direction(1, -1), // down-left
            Direction(0, -1),  // left
            Direction(-1, -1)  // up-left
    )
}

/**
 * The unicode symbol of the piece, or throws exception of the receiver piece is null
 * // TODO move somewhere else?
 */
val Piece?.unicode: String
    get() = when(this) {
        is Pawn -> if (player == Player.WHITE) "\u2659" else "\u265F"
        is Rook -> if (player == Player.WHITE) "\u2656" else "\u265C"
        is Knight -> if (player == Player.WHITE) "\u2658" else "\u265E"
        is Bishop -> if (player == Player.WHITE) "\u2657" else "\u265D"
        is Queen -> if (player == Player.WHITE) "\u2655" else "\u265B"
        is King -> if (player == Player.WHITE) "\u2654" else "\u265A"
        else -> throw IllegalArgumentException("Receiver piece is null")
    }
