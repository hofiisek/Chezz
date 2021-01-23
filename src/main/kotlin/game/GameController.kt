package game

import board.Board
import board.Square
import piece.moveTo


/**
 * @author Dominik Hoftych
 */
object GameController {

    var currentBoard: Board = Board()

    fun processMove(move: Move): Board {
        val (movingPiece, squareTo, capturedPiece) = move
        val squareFrom = currentBoard.getSquareFor(movingPiece)

        val updatedSquares: List<Square> = listOfNotNull(
                Square(squareFrom, null),
                Square(squareTo, movingPiece moveTo squareTo),
                move.isEnPassantMove.ifTrueOrNull {
                    Square(currentBoard.getSquareFor(capturedPiece!!), null)
                }
        )

        return Board(currentBoard, updatedSquares).also { currentBoard = it }
    }
}

/**
 * Performs given [action] if the receiver boolean is true, otherwise returns null
 */
private fun <T> Boolean.ifTrueOrNull(action: () -> T): T? = if (this) action() else null
