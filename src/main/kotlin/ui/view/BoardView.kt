package ui.view

import board.*
import javafx.geometry.HPos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import piece.Piece
import tornadofx.*
import ui.controller.BoardController

/**
 * Main view representing the chess board UI.
 *
 * @author Dominik Hoftych
 */
class BoardView : View() {

    private val controller: BoardController by inject()

    /**
     * Matrix of [Rectangle]s that represent individual squares of the board.
     */
    private val boardSquares: Matrix<Rectangle> = Matrix(8, 8) { _, _ ->
        Rectangle(0.0, 0.0, 80.0, 80.0).apply { arcWidth = 3.0; arcHeight = 3.0 }
    }

    /**
     * Matrix of [Label]s that contain the piece images.
     */
    private val boardPieces: Matrix<Label> = Matrix(8, 8) { _, _ ->
        Label().apply {
            GridPane.setHalignment(this, HPos.CENTER)
        }
    }

    /**
     * Root gridpane that consists of a 8x8 matrix of child panes, each of those representing
     * a single square on the chess board and listening for mouse left-clicks
     */
    override val root = gridpane {
        controller.getSquares().forEachRow { row ->
            row {
                row.map { initSquareUI(this, it) }
            }
        }

        redrawBoard(controller.getBoard())

        onRightClick {
            controller.resetSelection()
        }
    }

    /**
     * Initialize UI of given [square], i.e. add [Rectangle] and [Label] that represent
     * the given [square] to the parent pane and register mouse left-click listener
     */
    private fun initSquareUI(pane: Pane, square: Square) : Pane {
        // could not center the piece image other than using another nested gridpane :(
        return pane.gridpane {
            add(boardSquares[square.position])
            add(boardPieces[square.position])

            onLeftClick {
                controller.onSquareClicked(square)
            }
        }
    }

    /**
     * Renders the currently selected piece
     */
    fun renderSelectedPiece(selectedPiece: Piece) {
        boardSquares[selectedPiece.position].fill = Color.OLIVE
    }

    /**
     * Renders all allowed moves for the currently selected piece,
     * i.e. appropriately colors squares to which the piece can be moved.
     *
     * @param movePositions set of positions to which it's legal to move the piece
     */
    fun renderAllowedMoves(movePositions: Set<Position>) {
        movePositions.forEach {
            boardSquares[it].fill = Color.FORESTGREEN
        }
    }

    /**
     * Redraw the whole board, i.e. repaint squares and also render pieces.
     *
     * @param board current board state
     */
    fun redrawBoard(board: Board) {
        repaintBoard()
        board.squares.forEach { square ->
            redrawPiece(square)
        }
    }

    /**
     * Paints the board squares to default colors
     */
    fun repaintBoard() {
        boardSquares.forEachIndexed { row, col, square ->
            square.fill = getSquareColor(row, col)
        }
    }

    /**
     * Renders the piece on given [Square] if there is any, otherwise removes
     * the piece image from given square.
     */
    private fun redrawPiece(square: Square) {
        boardPieces[square.position].graphic = square.piece?.img
    }


    private fun getSquareColor(row: Int, col: Int): Color = if((row * 7 + col) % 2 < 1) Color.SANDYBROWN else Color.SADDLEBROWN


}