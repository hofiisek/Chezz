package ui.controller

import board.*
import game.*
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
     * The current board state
     */
    private var currentBoard: Board = Board()

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
     */
    fun onSquareClicked(clickedPosition: Position): RenderObject {
        val clickedSquare: Square = currentBoard[clickedPosition]

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
     * Selects given [piece]
     */
    private fun selectPiece(piece: Piece): RenderObject {
        if (piece.player != currentBoard.playerOnTurn) return RenderObject.Nothing

        val moves: Set<Move> = piece.getAllowedMoves(currentBoard)
        allowedMoves = moves.associateBy {
            when (it) {
                is BasicMove -> it.to.position
                is EnPassantMove -> it.to.position
                is CastlingMove -> it.king.second.position
            }
        }
        selectedPiece = piece

        return RenderObject.SelectedPiece(piece, allowedMoves.keys)
    }


    /**
     * Based on the [clickedSquare] either
     * - moves with the selected piece if the [clickedSquare] is occupied by the other player (assuming the move
     * won't put the king in check)
     * - selects the piece occupying the [clickedSquare] if it's of the same color
     * - does nothing if the [clickedSquare] is not in the set of allowed moves
     */
    private fun moveOrReselect(clickedSquare: Square): RenderObject {
        return when {
            clickedSquare occupiedBySamePlayerAs selectedPiece!! -> selectPiece(clickedSquare.piece!!)
            clickedSquare.position in allowedMoves -> {
                currentBoard = allowedMoves[clickedSquare.position]!!.applyOn(currentBoard)
                resetSelection()
                when {
                    currentBoard.isCheck() -> RenderObject.Check(currentBoard)
                    currentBoard.isCheckMate() -> RenderObject.Checkmate
                    currentBoard.isStaleMate() -> RenderObject.Stalemate
                    else -> RenderObject.UpdatedBoard(currentBoard)
                }
            }
            // TODO handle checkmate and stalemate
            // TODO render check
            else -> RenderObject.Nothing
        }
    }

}
