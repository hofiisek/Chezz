package ui.controller

import board.*
import game.GameController
import game.Move
import piece.Piece
import tornadofx.Controller
import ui.view.BoardView

/**
 * Main controller of the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardController : Controller() {

    /**
     * Reference to the board renderer.
     */
    private val boardView: BoardView by inject()

    private var selectedPiece: Piece? = null
    private var moves: Map<Position, Move> = emptyMap()

    /**
     * Mouse left-click listener registered on each square.
     * Is called whenever a piece is selected or a move with the selected piece
     * is performed.
     */
    fun onSquareClicked(clickedPosition: Position) {
        val clickedSquare: Square = getSquare(clickedPosition).also {
            println("Clicked on square $it")
        }

        if (selectedPiece == null) {
            if (clickedSquare.piece != null) {
                selectPiece(clickedSquare.piece)
            }
        } else {
            moveOrReselect(clickedSquare)
        }
    }

    /**
     * Mouse right-click listener registered on the whole board, used to reset (i.e. deselect) the currently selected piece.
     */
    fun resetSelection() {
        selectedPiece = null
        moves = emptyMap()
    }

    /**
     * Select given [Piece] and update the UI
     */
    private fun selectPiece(clickedPiece: Piece) {
        val allowedMoves: Set<Move> = clickedPiece.getAllowedMoves(GameController.currentBoard)
        selectedPiece = clickedPiece
        moves = allowedMoves.associateBy { it.to.position }

        renderCurrentSelection()
    }

    /**
     * Renders currently selected piece and all its allowed moves
     */
    private fun renderCurrentSelection() {
        boardView.repaintBoard()
        boardView.renderSelectedPiece(selectedPiece!!)
        boardView.renderAllowedMoves(moves.keys)
    }

    /**
     * Either move with the selected piece, or select different piece, or do nothing if the move is not allowed.
     */
    private fun moveOrReselect(clickedSquare: Square) {
        when {
            clickedSquare occupiedBySamePlayerAs selectedPiece!! -> selectPiece(clickedSquare.piece!!)
            clickedSquare.position in moves -> {
                val updatedBoard: Board = GameController.processMove(moves.getValue(clickedSquare.position))
                boardView.redrawBoard(updatedBoard)
                resetSelection()
            }
            else -> return
        }
    }

    fun getSquare(position: Position): Square = GameController.currentBoard[position]

}
