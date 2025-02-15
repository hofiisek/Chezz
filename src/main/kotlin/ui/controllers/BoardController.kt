package ui.controllers

import board.*
import game.*
import io.PgnExporter
import io.PgnImporter
import piece.*
import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.Scope
import ui.controllers.ViewUpdate.*
import ui.fragments.PromotionDialog
import ui.fragments.PromotionPieceModel
import ui.views.BoardView
import java.io.File
import kotlin.properties.Delegates.observable

/**
 * Main controller of the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardController : Controller() {

    /**
     * Reference to the board view where stuff is rendered
     */
    private val boardView: BoardView by inject()

    /**
     * The current board state. On each change, it resets the current selection,
     * updates the [boardView] according to the new board state, checks for promotion,
     * and checks whether the game hasn't ended yet
     */
    private var currentBoard: Board by observable(initialValue = Board.emptyBoard()) { _, _, newBoard ->
        boardView.updateView(BoardChanged(newBoard))
        checkGameOver()
        resetSelection()
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
    fun onSquareClicked(clickedPosition: Position) = currentBoard.getSquare(clickedPosition).let {
        when (selectedPiece) {
            is Piece -> if (it isOccupiedBy selectedPiece!!.player) {
                selectPiece(it.piece!!)
            } else {
                tryMoveTo(it)
            }
            else -> it.piece?.let { clickedPiece -> selectPiece(clickedPiece) }
        }
    }

    /**
     * Selects given [piece] and updates the view accordingly
     */
    private fun selectPiece(piece: Piece) {
        if (piece.player != currentBoard.playerOnTurn) return

        selectedPiece = piece
        allowedMovesBySquare = piece.getAllowedMoves(currentBoard).associateBy {
            when (it) {
                is BasicMove -> currentBoard.getSquare(it.to)
                is PromotionMove -> currentBoard.getSquare(it.basicMove.to)
                is EnPassantMove -> currentBoard.getSquare(it.to)
                is CastlingMove -> currentBoard.getSquare(it.kingDestination)
            }
        }

        boardView.updateView(
            PieceSelected(
                piece = piece,
                allowedMoves = allowedMovesBySquare.keys,
                checkedKing = if (currentBoard.isCheck()) currentBoard.getKing() else null
            )
        )
    }

    /**
     * Based on the [clickedSquare] either
     * - moves with the selected piece to the [clickedSquare] if it's unoccupied
     * - captures the opponent's piece currently occupying the [clickedSquare]
     * - does nothing if a move to the [clickedSquare] is not possible
     */
    private fun tryMoveTo(clickedSquare: Square) = allowedMovesBySquare[clickedSquare]?.let { move ->
        currentBoard = if (move is BasicMove && move.isPromotionMove) {
            currentBoard.playMove(PromotionMove(move, observePromotedPiece(move)))
        } else {
            currentBoard.playMove(move)
        }
    } ?: Unit

    /**
     * We need to observe the promotion to know the particular piece to which the player
     * chooses to promote its pawn to.
     * To achieve this, we need to open the [PromotionDialog] in a [Scope] and provide it
     * with an [ItemViewModel]. Then we are able to retrieve whatever gets changed
     * and committed to that model in that scope.
     */
    private fun observePromotedPiece(move: BasicMove): Piece {
        val (pawn, destination) = move

        val model = PromotionPieceModel()
        boardView.openPromotionWindow(Scope(model), pawn moveTo destination)

        return model.pieceType.value
    }

    /**
     * Checks whether the game has ended, and if so, opens up the game-over window,
     * otherwise does nothing
     */
    private fun checkGameOver() = when (val result = getGameResult()) {
        GameResult.StillPlaying -> Unit
        else -> boardView.openGameOverWindow(result)
    }

    /**
     * Mouse right-click listener registered on the whole board,
     * used to reset (i.e. deselect) the currently selected piece
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
    fun startGame(gameState: Board = Board.initialBoard()) {
        currentBoard = gameState
    }

    /**
     * Returns true if the game is currently being played
     */
    fun hasGameStarted(): Boolean = currentBoard != Board.emptyBoard()

    /**
     * Returns a [GameResult] describing the current state of the [currentBoard]
     */
    fun getGameResult(): GameResult = currentBoard.getGameResult()

    /**
     * Undoes the last move, or does nothing if the game hasn't started yet
     */
    fun undoLastMove() {
        if (hasGameStarted()) currentBoard.previousBoard?.apply {
            currentBoard = this
        }
    }

    /**
     * Processes given [pgnFile] and initializes the board in the state represented
     * by the pgn file
     */
    fun importPgn(pgnFile: File) {
        currentBoard = PgnImporter.importPgn(pgnFile)
    }

    /**
     * Returns the PGN "movetext" representation of the [currentBoard]
     */
    fun exportPgn(): String = PgnExporter.exportToPgn(currentBoard)
}
