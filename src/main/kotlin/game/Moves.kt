package game

import board.Board
import board.Square
import board.updateWith
import piece.Piece
import piece.moveTo

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
 * Basic move with the [piece] to the [to] square. May be a capture move if there's an enemy piece.
 */
data class BasicMove(val piece: Piece, val to: Square): Move() {
    override val an: String = "${piece.an}${if (to.piece != null) "x" else ""}${to.an}"
}

/**
 * The castling move, which is the only move involving two pieces at once - [rook] and [king].
 * When the king moves left, the castling is said to be [queenSide].
 */
data class CastlingMove(val rook: Pair<Piece, Square>, val king: Pair<Piece, Square>, val queenSide: Boolean) : Move() {
    override val an: String = "0-0${if (queenSide) "-0" else ""}"
}

/**
 * The en passant move, during which the moving [pawn] captures the [capturedPawn] but ends up in a different
 * square [to].
 */
data class EnPassantMove(val pawn: Piece, val to: Square, val capturedPawn: Piece) : Move() {
    override val an: String = "${pawn.position.file}x${to.an} e.p."
}

/**
* Applies the move to the given [board] and returns an updated board
*/
fun Move.applyOn(board: Board, simulate: Boolean = false): Board {
    val updatedSquares: List<Square> = when (this) {
        is BasicMove -> processBasicMove(board, this)
        is CastlingMove -> processCastling(board, this)
        is EnPassantMove -> processEnPassant(board, this)
    }
    return board.updateWith(squares = updatedSquares, takeTurns = !simulate)
}


/**
 * Returns a list of squares affected when the given [move] move is applied to the given [board]
 */
private fun processBasicMove(board: Board, move: BasicMove): List<Square> {
    val (piece, destinationSquare) = move
    val originSquare = board.getSquareFor(piece)
    return listOf(
            Square(originSquare, null),
            Square(destinationSquare, piece moveTo destinationSquare),
    )
}

/**
 * Returns a list of squares affected when the given [castling] move is applied to the given [board]
 */
private fun processCastling(board: Board, castling: CastlingMove): List<Square> {
    val (rook, rookDestinationSquare) = castling.rook
    val (king, kingDestinationSquare) = castling.king
    return listOf(
            Square(board.getSquareFor(rook), null),
            Square(board.getSquareFor(king), null),
            Square(rookDestinationSquare, rook moveTo rookDestinationSquare),
            Square(kingDestinationSquare, king moveTo kingDestinationSquare)
    )
}

/**
 * Returns a list of squares affected when the given [enPassant] move is applied to the given [board]
 */
private fun processEnPassant(board: Board, enPassant: EnPassantMove): List<Square> {
    val (pawn, destinationSquare, capturedPawn) = enPassant
    val originSquare = board.getSquareFor(pawn)
    return listOf(
            Square(originSquare, null),
            Square(board.getSquareFor(capturedPawn), null),
            Square(destinationSquare, pawn moveTo destinationSquare)
    )
}