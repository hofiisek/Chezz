package game

import board.Board
import board.Position
import board.Square
import piece.King
import piece.Piece

/**
 * Returns true if the position is in check w.r.t given [board] state
 */
fun Position.isInCheck(board: Board): Boolean {
    val enemyPieces: List<Piece> = board.getPiecesFor(board.playerOnTurn.theOtherPlayer)
    return enemyPieces
        .map { it.getAllowedMoves(board = board, validateForCheck = false) }
        .flatten()
        .mapNotNull {
            // we are only concerned about basic moves, because kings can't be captured en passant,
            // and castling can't capture at all
            when (it) {
                is BasicMove -> it.to.position
                else -> null
            }
        }.contains(this)
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
fun Board.isCheck(): Boolean {
    val king = getKing()
    return king.position.isInCheck(this)
}

/**
 * Returns true if the king of the player on turn is not in check
 */
fun Board.isNotCheck(): Boolean = !isCheck()

/**
 * Returns true if the king of the player on turn has been checkmated
 */
fun Board.isCheckMate(): Boolean {
    val king = getKing()
    return king.isInCheck(this) && king.getAllowedMoves(this).isEmpty()
}

/**
 * Returns true if the stalemate occurred
 */
fun Board.isStaleMate(): Boolean {
    val king = getKing()
    //return !king.isInCheck(this) && king.getAllowedMoves(this).isEmpty()
    return false
}

/**
 * Returns the king of the player on turn
 */
fun Board.getKing(): Piece = getPiecesFor(playerOnTurn, King::class).first()

