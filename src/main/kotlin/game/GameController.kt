package game

import board.Board
import board.printUnicode
import piece.moveTo
import tornadofx.find
import ui.controller.BoardController


/**
 * @author Dominik Hoftych
 */
object GameController {

    val board: Board = Board().also { it.printUnicode() }

    val boardController: BoardController = find(BoardController::class)

    fun processMove(move: Move): Board {
        val (movingPiece, squareTo) = move
        val squareFrom = board.getSquare(movingPiece.position)

        squareFrom.piece = null
        squareTo.piece = movingPiece moveTo squareTo

        return board
    }



}