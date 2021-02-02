package piece

import board.Board
import board.Position
import board.Square
import board.add
import game.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.lang.IllegalArgumentException

/**
 * Abstract parent of all chess pieces, i.e. pawn, rook, bishop, knight, queen and king.
 *
 * @param player [Player] who owns this piece
 * @param position [Position] of this piece
 * @param history list of all previous [Position]s of this piece
 * 
 * @author Dominik Hoftych
 */
sealed class Piece(
        open val player: Player,
        open val position: Position,
        open val history: MutableList<Position>
) {

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
     * [ImageView] with the image of the piece
     */
    val img: ImageView by lazy {
        ImageView(Image("/pieces/${name}.png", 40.0, 40.0, true, true))
    }

    /**
     * Movement of the piece defined as a set of directions along x and y axis respectively.
     * Defaults to an empty set but is overridden by each piece type except for pawn, whose movement
     * is a bit more complex.
     */
    open val movement: Set<Direction> = emptySet()

    /**
     * Returns the set of allowed moves of this piece w.r.t. given board
     */
    fun getAllowedMoves(board: Board): Set<Move> = MoveGenerator.generate(this, board)
}

/**
 * The opposite [Player], i.e. the player who does NOT own the piece
 */
val Piece.theOtherPlayer: Player
    get() = if (this.player == Player.WHITE) Player.BLACK else Player.WHITE

/**
 * The unicode symbol of the piece, or throws exception of the receiver piece is null
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

/**
 * Moves with the piece to given [square].
 * A new instance of [Piece] is initialized on its new position and the move
 * is recorded in piece's history list.
 */
infix fun Piece.moveTo(square: Square): Piece = when(this) {
    is Pawn -> Pawn(this.player, square.position, this.history plus square.position)
    is Rook -> Rook(this.player, square.position, this.history plus square.position)
    is Knight -> Knight(this.player, square.position, this.history plus square.position)
    is Bishop -> Bishop(this.player, square.position, this.history plus square.position)
    is Queen -> Queen(this.player, square.position, this.history plus square.position)
    is King -> King(this.player, square.position, this.history plus square.position)
}

/**
 * Infixed convenience method for [List.plus] that can be applied on [MutableList]
 */
infix fun <T> MutableList<T>.plus(element: T): MutableList<T> {
    this.add(element)
    return this
}


data class Pawn(
        override val player: Player,
        override var position: Position,
        override val history: MutableList<Position> = mutableListOf()
) : Piece(player, position, history) {

    override val name: String = "Pawn_${player.color()}"

    override val an: String = "P"

    /**
     * Helper property when evaluating the possibility of en passant moves.
     *
     * If the pawn advanced two squares as his last move, the returned position
     * is the position of the square that was skipped during the two-square move,
     * otherwise it's null.
     */
    val advancedTwoSquares: Position?
        get() = when {
            history.size != 1 -> null
            player == Player.BLACK && position.row != 3 -> null
            player == Player.WHITE && position.row != 4 -> null
            else -> {
                val shiftX = if (player == Player.WHITE) 1 else -1
                (position add Position(shiftX, 0))
            }
        }
}

data class Rook(
        override val player: Player,
        override var position: Position,
        override val history: MutableList<Position> = mutableListOf()
) : Piece(player, position, history) {

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
        override var position: Position,
        override val history: MutableList<Position> = mutableListOf()
) : Piece(player, position, history) {

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
        override var position: Position,
        override val history: MutableList<Position> = mutableListOf()
) : Piece(player, position, history) {

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
        override var position: Position,
        override val history: MutableList<Position> = mutableListOf()
) : Piece(player, position, history) {

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
        override var position: Position,
        override val history: MutableList<Position> = mutableListOf()
) : Piece(player, position, history) {

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
