package game

import board.Board
import board.Square
import board.updateWith
import piece.moveTo


/**
 *
 * TODO javadoc
 * TODO rename this class somehow..
 *
 * @author Dominik Hoftych
 */
object GameController {

    /**
     * The current board state
     */
    var currentBoard: Board = Board()

    /**
     * Applies the given [move] to the current board state and returns
     * an updated board state
     */
    fun processMove(move: Move): Board {
        val updatedSquares: List<Square> = when (move) {
            is BasicMove -> processBasicMove(move)
            is CastlingMove -> processCastling(move)
            is EnPassantMove -> processEnPassant(move)
        }

        return currentBoard.updateWith(updatedSquares).also { currentBoard = it }
    }

    /**
     * Returns a list of squares affected by the given [basicMove]
     */
    private fun processBasicMove(basicMove: BasicMove): List<Square> {
        val (movingPiece, squareTo) = basicMove
        val squareFrom = currentBoard.getSquareFor(movingPiece)
        return listOf(
                Square(squareFrom, null),
                Square(squareTo, movingPiece moveTo squareTo),
        )
    }

    /**
     * Returns a list of squares affected by the given [castlingMove]
     */
    private fun processCastling(castlingMove: CastlingMove): List<Square> {
        val (rook, rookTo) = castlingMove.rook
        val (king, kingTo) = castlingMove.king
        return listOf(
                Square(currentBoard.getSquareFor(rook), null),
                Square(rookTo, rook moveTo rookTo),
                Square(currentBoard.getSquareFor(king), null),
                Square(kingTo, king moveTo kingTo)
        )
    }

    /**
     * Returns a list of squares affected by the given [enPassantMove]
     */
    private fun processEnPassant(enPassantMove: EnPassantMove): List<Square> {
        val (pawn, squareTo, capturedPawn) = enPassantMove
        val squareFrom = currentBoard.getSquareFor(pawn)
        return listOf(
                Square(squareFrom, null),
                Square(squareTo, pawn moveTo squareTo),
                Square(currentBoard.getSquareFor(capturedPawn), null)
        )
    }
}