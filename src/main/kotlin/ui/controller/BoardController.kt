package ui.controller

import board.Board
import board.Matrix
import board.Square
import board.belongsToSamePlayerAs
import game.GameController
import game.Move
import piece.Piece
import piece.getAllowedMoves
import tornadofx.Controller
import ui.view.BoardView

/**
 * Main controller of the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardController : Controller() {

    private val boardView: BoardView by inject()

    /**
     * Currently selected piece
     */
    private var selectedPiece: Piece? = null

    /**
     * Mouse left-click listener registered on each square.
     * Is called whenever a piece is selected or a move with the selected piece
     * is performed.
     */
    fun onSquareClicked(clickedSquare: Square) {
        println("Clicked on square $clickedSquare")

        if (selectedPiece == null) {
            if (clickedSquare.piece != null) selectPiece(clickedSquare.piece!!)
        } else {
            moveOrReselect(clickedSquare)
        }
    }

    /**
     * Mouse right-click listener registered on each square.
     * Is called whenever a piece is deselected.
     */
    fun resetSelection() {
        selectedPiece = null
        boardView.repaintBoard()
    }

    /**
     * Select given [Piece] and render allowed moves
     */
    private fun selectPiece(clickedPiece: Piece) {
        boardView.repaintBoard()

        selectedPiece = clickedPiece
        boardView.renderSelectedPiece(selectedPiece!!)

        val allowedMoves: Set<Move> = selectedPiece.getAllowedMoves(GameController.board)
        boardView.renderAllowedMoves(allowedMoves)
    }

    /**
     * Either move with the selected piece, or select different piece.
     */
    private fun moveOrReselect(clickedSquare: Square) {
        if (clickedSquare belongsToSamePlayerAs selectedPiece!!) {
            selectPiece(clickedSquare.piece!!)
        } else {
            val newBoardState: Board = GameController.processMove(Move(selectedPiece!!, clickedSquare))
            boardView.redrawBoard(newBoardState)
            resetSelection()
        }
    }

    fun getSquares(): Matrix<Square> = GameController.board.squares

    fun getBoard(): Board = GameController.board

}