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
        .flatMap { it.getAllowedMoves(board = board, validateForCheck = false) }
        .mapNotNull {
            // we are only concerned about basic moves, because kings can't be captured en passant ,
            // and castling can't capture at all
            // TODO think about this
            when (it) {
                is BasicMove -> it.to
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
fun Board.isCheckmate(): Boolean {
    val king = getKing()
    // TODO check whether some other piece can't capture the checking piece
    return isCheck() && king.getAllowedMoves(this).isEmpty()
}

/**
 * Returns true if the stalemate occurred
 */
fun Board.isStalemate(): Boolean {
    return !isCheck() && getPiecesFor(playerOnTurn).all { it.getAllowedMoves(this).isEmpty() }
}

/**
 * Returns the king of the player on turn
 */
fun Board.getKing(): Piece = getPiecesFor(playerOnTurn, King::class).first()

