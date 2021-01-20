package ui.controller

import board.*
import game.GameController
import game.Move
import piece.Piece
import tornadofx.Controller
import ui.controller.BoardController.CurrentSelection.*
import ui.view.BoardView
import kotlin.IllegalStateException

/**
 * Main controller of the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardController : Controller() {

    private val boardView: BoardView by inject()

    /**
     * Currently selected piece and its allowed moves, or [empty][CurrentSelection.Empty] if no piece is selected
     * at the moment
     */
    private var currentSelection: CurrentSelection = Empty

    /**
     * Mouse left-click listener registered on each square.
     * Is called whenever a piece is selected or a move with the selected piece
     * is performed.
     */
    fun onSquareClicked(clickedSquare: Square) {
        println("Clicked on square $clickedSquare")

        if (currentSelection is Empty) {
            if (clickedSquare.piece != null) {
                selectPiece(clickedSquare.piece!!)
            }
        } else {
            moveOrReselect(clickedSquare)
        }
    }

    /**
     * Mouse right-click listener registered on each square.
     * Is called whenever a piece is deselected.
     */
    fun resetSelection() {
        currentSelection = Empty
        boardView.repaintBoard()
    }

    /**
     * Select given [Piece] and update UI
     */
    private fun selectPiece(clickedPiece: Piece) {
        val allowedMoves: Set<Move> = clickedPiece.getAllowedMoves(GameController.board)
        currentSelection = SomePiece(clickedPiece, allowedMoves.associateBy { it.to.position })

        renderCurrentSelection()
    }

    /**
     * Renders currently selected piece and all its allowed moves
     */
    private fun renderCurrentSelection() {
        boardView.repaintBoard()
        boardView.renderSelectedPiece(currentSelection.selectedPiece)
        boardView.renderAllowedMoves(currentSelection.allowedMovesByPosition.keys)
    }

    /**
     * Either move with the selected piece, or select different piece, or do nothing if the move is not allowed.
     */
    private fun moveOrReselect(clickedSquare: Square) {
        when {
            clickedSquare occupiedBySamePlayerAs currentSelection.selectedPiece -> selectPiece(clickedSquare.piece!!)
            clickedSquare.position in currentSelection.allowedMovesByPosition -> {
                val newBoardState: Board = GameController.processMove(currentSelection.allowedMovesByPosition.getValue(clickedSquare.position))
                boardView.redrawBoard(newBoardState)
                resetSelection()
            }
            else -> return
        }
    }

    fun getSquares(): Matrix<Square> = GameController.board.squares

    fun getBoard(): Board = GameController.board

    /**
     * Experimental (and weird) workaround to avoid non-null assertions when working with the
     * currently selected piece, which is nullable, while we are sure that it is non-null.
     *
     * It also works as a sort of cache of the allowed moves.
     */
    private sealed class CurrentSelection {
        /**
         * No piece is selected at the moment
         */
        object Empty : CurrentSelection()

        /**
         * Given [piece] is currently selected and [moves] are its allowed moves
         */
        data class SomePiece(val piece: Piece, val moves: Map<Position, Move>) : CurrentSelection()
    }

    /**
     * Returns the currently selected piece assuming the current selection is not empty, throw otherwise
     */
    private val CurrentSelection.selectedPiece: Piece
        get() = if (this is SomePiece) this.piece else throw IllegalStateException("Current selection is empty")

    /**
     * Returns the allowed moves for the currently selected piece assuming it's not empty, throw otherwise
     */
    private val CurrentSelection.allowedMovesByPosition: Map<Position, Move>
        get() = if (this is SomePiece) moves else throw IllegalStateException("Current selection is empty")

}
