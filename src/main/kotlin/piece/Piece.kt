package piece

import board.Board
import board.Position
import board.Square
import board.add
import game.Move
import game.MoveGenerator
import game.Player
import game.color
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.lang.IllegalArgumentException

/**
 * Abstract parent of all chess pieces, i.e. pawn, rook, bishop, knight, queen and king.
 *
 * @param player [Player] who owns this piece
 * @param position [Position] of this piece
 * @param moveHistory list of all previous [Position]s of this piece
 * 
 * @author Dominik Hoftych
 */
sealed class Piece(
        open val player: Player,
        open val position: Position,
        open val moveHistory: MutableList<Position>
) {

    /**
     * Name of the piece in the "pieceType_colorLetter" format, e.g. Rook_b or King_w.
     * Is used when loading piece images so it MUST correspond.
     */
    abstract val name: String

    /**
     * Whether the piece has already moved from its original position
     */
    val hasMoved: Boolean
        get() = moveHistory.isNotEmpty()

    /**
     * [ImageView] with the image of the piece
     */
    val img: ImageView by lazy {
        ImageView(Image("/pieces/${name}.png", 40.0, 40.0, true, true))
    }

    /**
     * Returns the set of allowed moves of this piece w.r.t. given board
     */
    fun getAllowedMoves(board: Board): Set<Move> = MoveGenerator.generate(this, board)
}

/**
 * Returns the other [Player] of the receiver [Piece]
 */
val Piece.theOtherPlayer: Player
    get() = if (this.player == Player.WHITE) Player.BLACK else Player.WHITE

/**
 * Unicode symbol of the piece or empty string if the receiver is null
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
 * Moves with the piece to given [Square].
 * A new instance of [Piece] is initialized on its new position and the move
 * is recorded in piece's history list.
 */
infix fun Piece.moveTo(square: Square): Piece = when(this) {
    is Pawn -> Pawn(this.player, square.position, this.moveHistory plus square.position)
    is Rook -> Rook(this.player, square.position, this.moveHistory plus square.position)
    is Knight -> Knight(this.player, square.position, this.moveHistory plus square.position)
    is Bishop -> Bishop(this.player, square.position, this.moveHistory plus square.position)
    is Queen -> Queen(this.player, square.position, this.moveHistory plus square.position)
    is King -> King(this.player, square.position, this.moveHistory plus square.position)
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
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {

    override val name = "Pawn_${player.color()}"

    /**
     * Helper property when evaluating the possibility of en passant moves.
     *
     * The first value is true if and only if the pawn advanced two squares as his last move.
     * In such case, the second value is the position of the square that was skipped
     * during the two-square move, otherwise it's just a placeholder.
     */
    val advancedTwoSquares: Pair<Boolean, Position>
        get() = when {
            moveHistory.size != 1 -> false to position
            player == Player.BLACK && position.row != 3 -> false to position
            player == Player.WHITE && position.row != 4 -> false to position
            else -> {
                val shiftX = if (player == Player.WHITE) 1 else -1
                true to (position add Position(shiftX, 0))
            }
        }
}

data class Rook(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Rook_${player.color()}"
}

data class Knight(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Knight_${player.color()}"
}

data class Bishop(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Bishop_${player.color()}"
}

data class Queen(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Queen_${player.color()}"
}

data class King(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "King_${player.color()}"
}
