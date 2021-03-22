package game

import board.Board
import board.Position
import board.Square
import piece.Piece

/**
 * Returns true if the position is in check w.r.t given [board] state
 *
 * TODO what about promotion moves?
 */
fun Position.isInCheck(board: Board): Boolean {
    val enemyPieces: List<Piece> = board.getPieces(board.playerOnTurn.theOtherPlayer)
    return enemyPieces
        .flatMap { it.getAllowedMoves(board = board, validateForCheck = false) }
        .filterIsInstance<BasicMove>()
        .any { it.to == this }
}

/**
 * Returns true if the square is in check w.r.t given [board] state
 */
fun Square.isInCheck(board: Board) = position.isInCheck(board)

/**
 * Returns true if the piece is in check w.r.t given [board] state
 */
fun Piece.isInCheck(board: Board) = position.isInCheck(board)

/**
 * Returns true if the king of the player on turn is in check
 */
fun Board.isCheck() = getKing().position.isInCheck(this)

/**
 * Returns true if the king of the player on turn is not in check
 */
fun Board.isNotCheck() = !isCheck()

/**
 * Returns true if the king of the player on turn has been checkmated
 */
fun Board.isCheckmate() = isCheck() && getPieces(playerOnTurn).all { it.getAllowedMoves(this).isEmpty() }

/**
 * Returns true if the stalemate occurred
 */
fun Board.isStalemate() = !isCheck() && getPieces(playerOnTurn).all { it.getAllowedMoves(this).isEmpty() }
