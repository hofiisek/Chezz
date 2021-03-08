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
     * Processes the given [board] and returns its PGN "movetext" representation
     */
    fun exportToPgn(board: Board): String {
        return exportRecursive(Board.initialBoard(), board.playedMoves.iterator(), "")
    }

    /**
     * Recursively accumulates the given [movetext] string by performing given [moves] one by one
     * on the current [board]. The current board state needs to be known throughout the
     * recursion, to be able to resolve situations where multiple pieces of the same type
     * can move to the same destination, which would cause their standard algebraic
     * notations to be unambiguous.
     */
    private tailrec fun exportRecursive(board: Board, moves: Iterator<Move>, movetext: String): String {
        if (!moves.hasNext()) return "$movetext${board.getGameResult().asString()}"

        val move = moves.next()
        val newBoard = board.playMove(move)

        val roundNumOrEmpty = if (board.whiteOnTurn()) "${newBoard.playedMoves.size / 2 + 1}. " else ""
        val checkOrCheckmateSignOrEmpty = when {
            newBoard.isCheckmate() -> "#"
            newBoard.isCheck() -> "+"
            else -> ""
        }

        val toAppend = "$roundNumOrEmpty${move.getAlgebraicNotation(board)}$checkOrCheckmateSignOrEmpty "
        val newlineOrEmpty = if (movetext.charsSinceNewline() + toAppend.length > 80) "\n" else ""

        return exportRecursive(newBoard, moves, "$movetext$newlineOrEmpty$toAppend")
    }

    private fun String.charsSinceNewline(): Int {
        return length - maxOf(0, lastIndexOf('\n'))
    }
}