package ui.controller

import board.*
import game.*
import piece.Piece
import tornadofx.Controller
import ui.controller.ViewUpdate.*
import ui.controller.ViewUpdate.Nothing
import ui.view.BoardView
import kotlin.properties.Delegates.observable

/**
 * Main controller of the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardController : Controller() {

    /**
     * The current board state
     */
    private var currentBoard: Board by observable(initialValue = Board.EMPTY) { _, _, _ ->
        resetSelection()
        // TODO consider calling view.updateView here? make it more observer pattern
    }

    /**
     * Currently selected piece, or null if no piece is selected
     * TODO could be observable and calling view.updateView?
     */
    private var selectedPiece: Piece? = null

    /**
     * Allowed moves of the currently selected piece, or empty map if no piece is selected
     */
    private var allowedMovesBySquare: Map<Square, Move> = emptyMap()

    /**
     * Mouse left-click listener registered on each square.
     * The returned [ViewUpdate] contains all necessary data for the [BoardView]
     * to know what to render.
     */
    fun onSquareClicked(clickedPosition: Position): ViewUpdate {
        val clickedSquare: Square = currentBoard.getSquare(clickedPosition)

        println("Clicked on square $clickedSquare")

        return when(selectedPiece) {
            null -> if (clickedSquare.piece == null) Nothing else selectPiece(clickedSquare.piece)
            else -> moveOrReselect(clickedSquare)
        }
    }

    /**
     * Selects given [piece] and returns a [ViewUpdate] with the selected piece,
     * its allowed moves, and the king in check, if there is any
     */
    private fun selectPiece(piece: Piece): ViewUpdate {
        if (piece.player != currentBoard.playerOnTurn) return Nothing

        selectedPiece = piece
        allowedMovesBySquare = piece.getAllowedMoves(currentBoard).associateBy {
            when (it) {
                is BasicMove -> currentBoard.getSquare(it.to)
                is EnPassantMove -> currentBoard.getSquare(it.to)
                is CastlingMove -> currentBoard.getSquare(it.king.second)
            }
        }

        return PieceSelected(
            piece = piece,
            allowedMoves = allowedMovesBySquare.keys,
            checkedKing = if (currentBoard.isCheck()) currentBoard.getKing() else null
        )
    }

    /**
     * Based on the [clickedSquare] either
     * - moves with the selected piece if the [clickedSquare] is occupied by the other player (assuming the move
     * won't put the king in check)
     * - selects the piece occupying the [clickedSquare] if it's of the same color
     * - does nothing if the [clickedSquare] is not in the set of allowed moves
     */
    private fun moveOrReselect(clickedSquare: Square): ViewUpdate {
        return when {
            clickedSquare occupiedBySamePlayerAs selectedPiece!! -> selectPiece(clickedSquare.piece!!)
            clickedSquare in allowedMovesBySquare -> {
                currentBoard = currentBoard.playMove(allowedMovesBySquare.getValue(clickedSquare))
                BoardChanged(currentBoard)
            }
            else -> Nothing
        }
    }

    /**
     * Mouse right-click listener registered on the whole board, used to reset (i.e. deselect) the currently selected piece
     */
    fun resetSelection() {
        selectedPiece = null
        allowedMovesBySquare = emptyMap()
    }

    /**
     * Wipes any current game state and runs a new game starting in given [gameState].
     * If no [gameState] is provided, a fresh game is started with pieces in their
     * initial positions and white player on turn.
     */
    fun startGame(gameState: Board): ViewUpdate {
        currentBoard = gameState
        return BoardChanged(currentBoard)
    }


    /**
     * Undoes the last move
     */
    fun undoLastMove(): ViewUpdate = when (currentBoard) {
        Board.EMPTY -> Nothing
        else -> {
            currentBoard = currentBoard.previousBoard ?: currentBoard
            BoardChanged(currentBoard)
        }
    }
}
