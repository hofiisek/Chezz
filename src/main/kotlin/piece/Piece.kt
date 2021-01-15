package piece

import board.Board
import board.Position
import game.Move
import game.MoveGenerator
import game.Player
import game.text
import javafx.scene.image.Image
import javafx.scene.image.ImageView

/**
 * Abstract parent of all chess pieces, i.e. pawn, rook, bishop, knight, queen and king.
 * Resides on a particular [Position] and belongs to one of the [Player]s.
 */
sealed class Piece(open val player: Player, open var position: Position) {

    /**
     * Name of the piece in the "pieceType_colorLetter" format.
     * Is used when loading piece images so it MUST correspond.
     */
    abstract val name: String

    /**
     * Log of [Move]s of the piece
     */
    val moveHistory: MutableList<Move> = mutableListOf()

    /**
     * Whether the piece has already moved from its original position
     */
    val hasMoved: Boolean = moveHistory.isNotEmpty()

    /**
     * [ImageView] with the image of the piece
     */
    val img: ImageView
        get() = ImageView(Image("/pieces/${name}.png", 40.0, 40.0, true, true))

    /**
     * Returns the set of allowed moves of this piece w.r.t. current game state
     */
    fun getAllowedMoves(board: Board): Set<Move> = MoveGenerator.generate(this, board)

    /**
     * Returns the unicode symbol of the piece
     */
    fun unicode(): String = when(this) {
        is Pawn -> if (player == Player.WHITE) "\u2659" else "\u265F"
        is Rook -> if (player == Player.WHITE) "\u2656" else "\u265C"
        is Knight -> if (player == Player.WHITE) "\u2658" else "\u265E"
        is Bishop -> if (player == Player.WHITE) "\u2657" else "\u265D"
        is Queen -> if (player == Player.WHITE) "\u2655" else "\u265B"
        is King -> if (player == Player.WHITE) "\u2654" else "\u265A"
    }
}

/**
 * Extension to allow calling [Piece.getAllowedMoves] when we are sure that the piece is non-null,
 * without the need of null-safe checks.
 */
fun Piece?.getAllowedMoves(board: Board): Set<Move> {
    return if (this == null) emptySet() else MoveGenerator.generate(this, board)
}

data class Pawn(override val player: Player, override var position: Position) : Piece(player, position) {
    override val name = "Pawn_${player.text()}"
}

data class Rook(override val player: Player, override var position: Position) : Piece(player, position) {
    override val name = "Rook_${player.text()}"
}

data class Knight(override val player: Player, override var position: Position) : Piece(player, position) {
    override val name = "Knight_${player.text()}"
}

data class Bishop(override val player: Player, override var position: Position) : Piece(player, position) {
    override val name = "Bishop_${player.text()}"
}

data class Queen(override val player: Player, override var position: Position) : Piece(player, position) {
    override val name = "Queen_${player.text()}"
}

data class King(override val player: Player, override var position: Position) : Piece(player, position) {
    override val name = "King_${player.text()}"
}
