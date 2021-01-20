package game

import board.Board
import piece.moveTo


/**
 * @author Dominik Hoftych
 */
object GameController {

    val board: Board = Board()

    fun processMove(move: Move): Board {
        val (movingPiece, squareTo, capturedPiece) = move

        val squareFrom = board.getSquareFor(movingPiece)
        squareFrom.piece = null
        squareTo.piece = movingPiece moveTo squareTo

        if (move.isEnPassantMove) {
            board.removePiece(capturedPiece!!.position)
        }

        return board
    }



}