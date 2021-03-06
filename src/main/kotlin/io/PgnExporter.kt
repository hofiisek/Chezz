package io

import board.Board
import board.playMove
import board.whiteOnTurn
import game.*

/**
 * Exporter to the standard Portable Game Notation (PGN) format.
 *
 * @author Dominik Hoftych
 */
object PgnExporter {

    /**
     * Returns a string with the PGN movetext representing the given [board].
     */
    fun exportToPgn(board: Board): String {
        return exportRecursive(Board.initialBoard(), board.playedMoves.iterator(), "")
    }

    /**
     * Recursively accumulates the given [pgn] string by performing given [moves] one by one
     * on the current [board]. The current board state needs to be known throughout the
     * recursion, to be able to resolve situations where multiple pieces of the same type
     * can move to the same destination, which would cause their standard algebraic
     * notations to be unambiguous.
     */
    private tailrec fun exportRecursive(board: Board, moves: Iterator<Move>, pgn: String): String {
        if (!moves.hasNext()) return "$pgn${board.getGameResult().pgnString()}"

        val move = moves.next()
        val newBoard = board.playMove(move)

        val roundNumOrEmpty = if (board.whiteOnTurn()) "${newBoard.playedMoves.size / 2 + 1}. " else ""
        val checkOrCheckmateSignOrEmpty = when {
            newBoard.isCheckmate() -> "#"
            newBoard.isCheck() -> "+"
            else -> ""
        }

        return exportRecursive(
            board = newBoard,
            moves = moves,
            pgn = "$pgn$roundNumOrEmpty${move.getAlgebraicNotation(board)}$checkOrCheckmateSignOrEmpty "
        )
    }


}