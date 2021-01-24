package game

import board.Square
import piece.Pawn
import piece.Piece

/**
 * Representing a single move with a piece.
 *
 * @param movingPiece the piece which is being moved with
 * @param to destination square of the move
 * @param capturedPiece piece which is captured by the move, which equals to the piece on [to] square except for
 * en passant moves
 *
 * @author Dominik Hoftych
 */
data class Move(val movingPiece: Piece, val to: Square, val capturedPiece: Piece? = to.piece) {

    /**
     * Whether this move is a capture move
     */
    val isCaptureMove: Boolean = capturedPiece != null

    /**
     * Whether this move is en passant move
     */
    val isEnPassantMove: Boolean = movingPiece is Pawn && isCaptureMove && to.piece != capturedPiece
}