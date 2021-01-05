package game

import board.Board
import board.Position
import piece.*

/**
 * Moves generator.
 *
 * @author Dominik Hoftych
 */
object MoveGenerator {

    fun generate(piece: Piece, board: Board): Set<Move> = when(piece) {
        is Pawn -> pawnMoves(piece, board)
        is Rook -> rookMoves(piece, board)
        is Knight -> knightMoves(piece, board)
        is Bishop -> bishopMoves(piece, board)
        is Queen -> queenMoves(piece, board)
        is King -> kingMoves(piece, board)
    }

    private fun pawnMoves(piece: Piece, board: Board): Set<Move> {
        return setOf()
    }

    private fun rookMoves(piece: Piece, board: Board): Set<Move> {
        return setOf()
    }

    private fun knightMoves(piece: Piece, board: Board): Set<Move> {
        return setOf()
    }

    private fun bishopMoves(piece: Piece, board: Board): Set<Move> {
        return setOf()
    }

    private fun queenMoves(piece: Piece, board: Board): Set<Move> {
        return setOf()
    }
    private fun kingMoves(piece: Piece, board: Board): Set<Move> {
        return setOf()
    }


}