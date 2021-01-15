package game

import board.Board
import piece.Piece
import tornadofx.find
import ui.controller.BoardController


/**
 * @author Dominik Hoftych
 */
object GameController {

    val board: Board = Board()

    val boardController: BoardController = find(BoardController::class)

    fun processMove(move: Move): Board {
        val (movingPiece, squareTo) = move
        val squareFrom = board.getSquare(movingPiece.position)

        squareFrom.piece = null
        squareTo.piece = movingPiece
        movingPiece.position = squareTo.position

        return board
    }



}