package game

import board.Position
import board.Square
import piece.Pawn
import piece.Piece
import piece.moveTo
import ui.dialogs.PromotionDialog

/**
 * Abstract parent of a single move with a piece, which can be either a basic move,
 * en passant move, or castling move.
 *
 * @author Dominik Hoftych
 */
sealed class Move

/**
 * Basic move with the [piece] to the [to] square. May be a capture move if there's an enemy piece.
 */
data class BasicMove(val piece: Piece, val to: Position, val isCapture: Boolean = false) : Move() {

    /**
     * Whether this move is a promotion move.
     */
    val isPromotionMove: Boolean = piece is Pawn && (to.row == 0 || to.row == 7)
}

/**
 * When the moving pawn of a [basicMove] reaches the first or eighth rank, it needs to be promoted
 * to a different piece of player's choice.
 *
 * The problem is that at the time when the allowed moves are generated, the player's choice
 * of the piece to promote to is not yet known. Due to that, a promotion move acts as a basic move,
 * until the player makes his decision in the [PromotionDialog], after which is the basic move
 * transformed to an instance of this class.
 */
data class PromotionMove(val basicMove: BasicMove, val promotedTo: Piece) : Move()

/**
 * The castling move, which is the only move involving two pieces at once - [rook] and [king].
 * When the king moves left, the castling is said to be [queenSide].
 */
data class CastlingMove(val rook: Pair<Piece, Position>, val king: Pair<Piece, Position>, val queenSide: Boolean) : Move()

/**
 * The en passant move, during which the moving [pawn] captures the [capturedPawn] but ends up in a different
 * square [to].
 */
data class EnPassantMove(val pawn: Piece, val to: Position, val capturedPawn: Piece) : Move()

/**
 * Performs the move on the given [board] and returns the list of squares affected
 */
fun Move.getAffectedSquares(): List<Square> = when (this) {
    is BasicMove -> {
        val (piece, destination) = this
        listOf(
            Square(piece.position, null),
            Square(destination, piece moveTo destination),
        )
    }
    is PromotionMove -> {
        val (basicMove, pawnPromotedTo) = this
        val (pawn, destination) = basicMove
        listOf(
            Square(pawn.position, null),
            Square(destination, pawnPromotedTo),
        )
    }
    is CastlingMove -> {
        val (rook, rookDestination) = this.rook
        val (king, kingDestination) = this.king
        listOf(
            Square(rook.position, null),
            Square(king.position, null),
            Square(rookDestination, rook moveTo rookDestination),
            Square(kingDestination, king moveTo kingDestination)
        )
    }
    is EnPassantMove -> {
        val (pawn, destination, capturedPawn) = this
        listOf(
            Square(pawn.position, null),
            Square(capturedPawn.position, null),
            Square(destination, pawn moveTo destination)
        )
    }
}

