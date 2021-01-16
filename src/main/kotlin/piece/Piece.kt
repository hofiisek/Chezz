package piece

import board.Board
import board.Position
import board.Square
import game.Move
import game.MoveGenerator
import game.Player
import game.text
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import sun.security.ec.point.ProjectivePoint

/**
 * Abstract parent of all chess pieces, i.e. pawn, rook, bishop, knight, queen and king.
 *
 * @param player [Player] who owns this piece
 * @param position [Position] of this piece
 * @param moveHistory list of all previous [Position]s of this piece.
 * 
 * @author Dominik Hoftych
 */
sealed class Piece(
        open val player: Player,
        open val position: Position,
        open val moveHistory: MutableList<Position>
) {

    /**
     * Name of the piece in the "pieceType_colorLetter" format.
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
     * Returns the set of allowed moves of this piece w.r.t. current game state
     */
    fun getAllowedMoves(board: Board): Set<Move> = MoveGenerator.generate(this, board)
}

/**
 * Moves with the piece to given [Square].
 * A new instance of [Piece] is initialized on its new position and this move
 * is recorded in piece's history list.
 */
infix fun Piece.moveTo(square: Square): Piece = when(this) {
    is Pawn -> Pawn(this.player, square.position, this.moveHistory + square.position)
    is Rook -> Rook(this.player, square.position, this.moveHistory + square.position)
    is Knight -> Knight(this.player, square.position, this.moveHistory + square.position)
    is Bishop -> Bishop(this.player, square.position, this.moveHistory + square.position)
    is Queen -> Queen(this.player, square.position, this.moveHistory + square.position)
    is King -> King(this.player, square.position, this.moveHistory + square.position)
}

operator fun <T> MutableList<T>.plus(element: T): MutableList<T> {
    this.add(element)
    return this
}

/**
 * Extension function to allow calling [Piece.getAllowedMoves] when we are sure that the piece is non-null,
 * without the need of null-safe checks.
 */
fun Piece?.getAllowedMoves(board: Board): Set<Move> {
    return this?.getAllowedMoves(board) ?: emptySet()
}

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
        else -> ""
    }

data class Pawn(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Pawn_${player.text()}"
}

data class Rook(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Rook_${player.text()}"
}

data class Knight(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Knight_${player.text()}"
}

data class Bishop(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Bishop_${player.text()}"
}

data class Queen(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "Queen_${player.text()}"
}

data class King(
        override val player: Player,
        override var position: Position,
        override val moveHistory: MutableList<Position> = mutableListOf()
) : Piece(player, position, moveHistory) {
    override val name = "King_${player.text()}"
}
