package game

import board.Square
import piece.Rook
import piece.King
import piece.Piece

/**
 * Abstract parent of a single move with a piece, which can be either a basic move,
 * en passant move, or castling move.
 *
 * @author Dominik Hoftych
 */
sealed class Move {

    /**
     * The move expressed in the algebraic notation
     */
    abstract val an: String
}


/**
 * Basic move with a piece, may be a capture move.
 *
 * @param piece the moving piece
 * @param to the destination square of the moving piece
 */
data class BasicMove(val piece: Piece, val to: Square): Move() {
    override val an: String = "${piece.an}${if (to.piece != null) "x" else ""}${to.an}"
}

/**
 * The castling move, which is the only move involving two pieces at once - [Rook], and the [King].
 *
 * @param rook the particular rook involved in the castling, paired with its destination square
 * @param king the king paired with its destination square
 */
data class CastlingMove(val rook: Pair<Piece, Square>, val king: Pair<Piece, Square>, val queenSide: Boolean) : Move() {
    override val an: String = "0-0${if (queenSide) "-0" else ""}"
}

/**
 * The en passant move, always a capture move.
 *
 * @param pawn the moving pawn
 * @param to the destination square of the
 * @param capturedPawn the enemy pawn captured "en passant"
 */
data class EnPassantMove(val pawn: Piece, val to: Square, val capturedPawn: Piece) : Move() {
    override val an: String = "${pawn.position.file}x${to.an} e.p."
}