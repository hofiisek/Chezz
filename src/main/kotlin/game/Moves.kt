package game

import board.Board
import board.Position
import board.Square
import piece.Piece
import piece.moveTo

/**
 * Abstract parent of a single move with a piece, which can be either a basic move,
 * en passant move, or castling move.
 * // TODO does move need to have TO as square, isnt position enough?
 * @author Dominik Hoftych
 */
sealed class Move {

    /**
     * The move expressed in the algebraic notation
     */
    abstract val an: String
}

/**
 * Basic move with the [piece] to the [to] square. May be a capture move if there's an enemy piece.
 */
data class BasicMove(val piece: Piece, val to: Position, val isCapture: Boolean = false): Move() {
    override val an: String = "${piece.an}${if (isCapture) "x" else ""}${to.an}"
}

/**
 * The castling move, which is the only move involving two pieces at once - [rook] and [king].
 * When the king moves left, the castling is said to be [queenSide].
 */
data class CastlingMove(val rook: Pair<Piece, Position>, val king: Pair<Piece, Position>, val queenSide: Boolean) : Move() {
    override val an: String = "0-0${if (queenSide) "-0" else ""}"
}

/**
 * The en passant move, during which the moving [pawn] captures the [capturedPawn] but ends up in a different
 * square [to].
 */
data class EnPassantMove(val pawn: Piece, val to: Position, val capturedPawn: Piece) : Move() {
    override val an: String = "${pawn.position.file}x${to.an} e.p."
}

/**
 * Performs the move on given [board] and returns the list of squares affected
 */
fun Move.perform(): List<Square> = when (this) {
    is BasicMove -> {
        val (piece, destination) = this
        val origin = piece.position
        listOf(
            Square(origin, null),
            Square(destination, piece moveTo destination),
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
        val origin = pawn.position
        listOf(
            Square(origin, null),
            Square(capturedPawn.position, null),
            Square(destination, pawn moveTo destination)
        )
    }
}

