package ui.views

import board.*
import game.*
import javafx.geometry.HPos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.StageStyle
import piece.Pawn
import piece.Piece
import piece.icon
import tornadofx.*
import ui.controllers.BoardController
import ui.controllers.ViewUpdate
import ui.controllers.ViewUpdate.*
import ui.controllers.ViewUpdate.Nothing
import ui.dialogs.EndgameDialog
import ui.dialogs.PromotionDialog

/**
 * Main view representing the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardView : View() {

    /**
     * Reference to controller in which the mouse events are handled
     */
    private val controller: BoardController by inject()

    /**
     * Matrix of [Rectangle]s that represent individual squares of the board
     */
    private val boardSquares: Matrix<Rectangle> = Matrix(8, 8) { _, _ ->
        Rectangle(0.0, 0.0, 80.0, 80.0).apply { arcWidth = 3.0; arcHeight = 3.0 }
    }

    /**
     * Matrix of [Label]s that contain the piece images
     */
    private val boardPieces: Matrix<Label> = Matrix(8, 8) { _, _ ->
        Label().apply { GridPane.setHalignment(this, HPos.CENTER) }
    }

    /**
     * Root grid pane that consists of a 8x8 matrix of child panes, each of those representing
     * a single square on the chess board.
     *
     * Each square registers a left-mouse click listener, which will either select given piece,
     * or move with the selected piece.
     */
    override val root = gridpane {
        Board.EMPTY.squares.forEachRow { rowSquares ->
            row { rowSquares.map { initSquare(this, it.position) } }
        }

        repaintBoard()

        onRightClick {
            controller.resetSelection()
            repaintBoard()
        }
    }

    /**
     * Initializes UI of the square on given [position] and registers mouse left-click listener
     */
    private fun initSquare(parent: Pane, position: Position) : Pane = parent.gridpane {
        add(boardSquares[position])
        add(boardPieces[position])

        onLeftClick {
            controller.onSquareClicked(position)
        }
    }


    /**
     * Updates the view according to given [update]
     */
    fun updateView(update: ViewUpdate) {
        when (update) {
            is Nothing -> Unit
            is BoardChanged -> {
                checkGameOver(update.board)
                redrawBoard(update.board)
                checkForPromotion(update.board)
            }
            is PieceSelected -> renderSelectedPiece(
                update.piece,
                update.allowedMoves,
                update.checkedKing
            )
        }
    }

    private fun checkForPromotion(board: Board) {
        // TODO possibly move to controller?
        board.getPiecesFor(board.playerOnTurn.theOtherPlayer, Pawn::class)
            .firstOrNull { it.position.row == 0 || it.position.row == 7 }
            ?.let {
                find<PromotionDialog>(PromotionDialog::pawnToPromote to it).openModal(StageStyle.UNDECORATED)
            }
    }

    /**
     * Renders the currently selected [piece] and [squares] to which it is allowed to move.
     */
    private fun renderSelectedPiece(piece: Piece, squares: Set<Square>, checkedKing: Piece?) {
        repaintBoard()
        if (checkedKing != null) {
            boardSquares[checkedKing.position].fill = Color.RED
        }
        boardSquares[piece.position].fill = Color.OLIVE
        squares.forEach { boardSquares[it.position].fill = Color.FORESTGREEN }
    }

    /**
     * Redraw the board according to the given [board], i.e. repaint squares to default colors and
     * also render pieces on their current positions
     */
    private fun redrawBoard(board: Board) {
        repaintBoard()
        board.squares.forEach { boardPieces[it.position].graphic = it.piece?.icon }
        if (board.isCheck()) {
            boardSquares[board.getKing().position].fill = Color.RED
        }
    }

    /**
     * Checks whether the game ends or continues
     */
    private fun checkGameOver(board: Board) {
        when {
            board.isCheckmate() -> endGame(GameResult.Checkmate(board.playerOnTurn.theOtherPlayer))
            board.isStalemate() -> endGame(GameResult.Stalemate)
        }
    }

    /**
     * Ends the game by popping up a window
     */
    private fun endGame(result: GameResult) {
        find<EndgameDialog>(EndgameDialog::result to result).openModal()
    }

    /**
     * Paints the board squares to default colors
     */
    private fun repaintBoard() {
        boardSquares.forEachIndexed { row, col, square ->
            square.fill = if((row * 7 + col) % 2 < 1) Color.SANDYBROWN else Color.SADDLEBROWN
        }
    }

}