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
     * Currently selected piece, or null if no piece is selected
     */
    private var selectedPiece: Piece? = null

    /**
     * Allowed moves of the currently selected piece, or empty map if no piece is selected
     */
    private var allowedMoves: Map<Position, Move> = emptyMap()

    /**
     * Mouse left-click listener registered on each square.
     * The returned [RenderObject] contains all necessary data for the [BoardView]
     * to know what to render.
     *
     * @param clickedPosition position of the square that was clicked on, which initiated this event
     */
    fun onSquareClicked(clickedPosition: Position): RenderObject {
        val clickedSquare: Square = getSquare(clickedPosition)

        println("Clicked on square $clickedSquare")

        return when(selectedPiece) {
            null -> if (clickedSquare.piece == null) RenderObject.Nothing else selectPiece(clickedSquare.piece)
            else -> moveOrReselect(clickedSquare)
        }
    }

    /**
     * Mouse right-click listener registered on the whole board, used to reset (i.e. deselect) the currently selected piece
     */
    fun resetSelection() {
        selectedPiece = null
        allowedMoves = emptyMap()
    }

    /**
     * Selects given [clickedPiece]
     */
    private fun selectPiece(clickedPiece: Piece): RenderObject {
        val moves: Set<Move> = clickedPiece.getAllowedMoves(GameController.currentBoard)
        selectedPiece = clickedPiece
        allowedMoves = moves.associateBy { it.to.position }

        return RenderObject.SelectedPiece(clickedPiece, allowedMoves.keys)
    }


    /**
     * Based on the [clickedSquare] does one of the following:
     * - moves with the selected piece if the [clickedSquare] is occupied by the other player (assuming the move
     * won't put the king in check)
     * - selects the piece occupying the [clickedSquare] if it's of the same color
     * - does nothing if the [clickedSquare] is not in the set of allowed moves
     */
    private fun moveOrReselect(clickedSquare: Square): RenderObject {
        return when {
            clickedSquare occupiedBySamePlayerAs selectedPiece!! -> selectPiece(clickedSquare.piece!!)
            clickedSquare.position in allowedMoves -> {
                val newBoard: Board = GameController.processMove(allowedMoves.getValue(clickedSquare.position))
                resetSelection()
                RenderObject.UpdatedBoard(newBoard)
            }
            else -> RenderObject.Nothing
        }
    }

    /**
     * Returns the [Square] on given [position]
     */
    private fun getSquare(position: Position): Square = GameController.currentBoard[position]

}
