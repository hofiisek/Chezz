package ui.controllers

import board.*
import game.*
import piece.Pawn
import piece.Piece
import tornadofx.Controller
import ui.controllers.ViewUpdate.*
import ui.views.BoardView
import kotlin.properties.Delegates.observable

/**
 * Main controller of the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardController : Controller() {

    private val boardView: BoardView by inject()

    /**
     * The current board state. On each change, it resets the current selection,
     * updates the [boardView] according to the new board state, checks for promotion,
     * and checks whether the game hasn't ended yet
     */
    private var currentBoard: Board by observable(initialValue = Board.EMPTY) { _, _, newBoard ->
        resetSelection()
        boardView.updateView(BoardChanged(newBoard))
        checkForPromotion()
        checkGameOver()
    }

    /**
     * Currently selected piece, or null if no piece is selected
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
    fun onSquareClicked(clickedPosition: Position) {
        val clickedSquare: Square = currentBoard.getSquare(clickedPosition)

        println("Clicked on square $clickedSquare")

        if (selectedPiece == null) {
            if (clickedSquare.piece != null) selectPiece(clickedSquare.piece)
        } else {
            moveOrReselect(clickedSquare)
        }
    }

    /**
     * Selects given [piece] and returns a [ViewUpdate] with the selected piece,
     * its allowed moves, and the king in check, if there is any
     */
    private fun selectPiece(piece: Piece) {
        if (piece.player != currentBoard.playerOnTurn) return

        selectedPiece = piece
        allowedMovesBySquare = piece.getAllowedMoves(currentBoard).associateBy {
            when (it) {
                is BasicMove -> currentBoard.getSquare(it.to)
                is EnPassantMove -> currentBoard.getSquare(it.to)
                is CastlingMove -> currentBoard.getSquare(it.king.second)
            }
        }

        boardView.updateView(PieceSelected(
            piece = piece,
            allowedMoves = allowedMovesBySquare.keys,
            checkedKing = if (currentBoard.isCheck()) currentBoard.getKing() else null
        ))
    }

    /**
     * Based on the [clickedSquare] either
     * - moves with the selected piece if the [clickedSquare] is occupied by the other player (assuming the move
     * won't put the king in check)
     * - selects the piece occupying the [clickedSquare] if it's of the same color
     * - does nothing if the [clickedSquare] is not in the set of allowed moves
     */
    private fun moveOrReselect(clickedSquare: Square) {
        when {
            clickedSquare occupiedBySamePlayerAs selectedPiece!! -> selectPiece(clickedSquare.piece!!)
            clickedSquare in allowedMovesBySquare -> {
                currentBoard = currentBoard.playMove(allowedMovesBySquare.getValue(clickedSquare))
            }
        }
    }

    /**
     * Checks whether the game has ended and call board view if necessary
     */
    private fun checkGameOver() {
        when {
            currentBoard.isCheckmate() -> boardView.endTheGame(GameResult.Checkmate(currentBoard.playerOnTurn.theOtherPlayer))
            currentBoard.isStalemate() -> boardView.endTheGame(GameResult.Stalemate)
        }
    }

    /**
     * Checks whether some pawn needs to be promotion and call board view if necessary
     */
    private fun checkForPromotion() {
        currentBoard.getPiecesFor(currentBoard.playerOnTurn.theOtherPlayer, Pawn::class)
            .firstOrNull { it.position.row == 0 || it.position.row == 7 }
            ?.let {
                boardView.promotePawn(it)
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
     * Wipes any current game state and runs game starting in given [gameState].
     * If no [gameState] is provided, a fresh game is started with pieces in their
     * initial positions and white player on turn.
     */
    fun startGame(gameState: Board = Board.INITIAL) {
        currentBoard = gameState
    }


    /**
     * Undoes the last move, or does nothing if the game hasn't started yet
     */
    fun undoLastMove() {
        if (currentBoard != Board.EMPTY) {
            currentBoard = currentBoard.previousBoard ?: currentBoard
        }
    }

    /**
     * Promotes the pawn to the [promotedPiece]. The pawn being promoted is the pawn
     * currently occupying the given [promotedPiece]'s position.
     */
    fun promotePawn(promotedPiece: Piece) {
        currentBoard = currentBoard.promote(promotedPiece)
    }
}
