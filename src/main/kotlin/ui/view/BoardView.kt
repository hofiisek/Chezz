package ui.view

import board.*
import game.Move
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
        boardSquares[selectedPiece.position.row][selectedPiece.position.col].fill = Color.OLIVE
    }

    /**
     * Renders all moves that are allowed for the currently selected piece.
     */
    fun renderAllowedMoves(moves: Set<Move>) {
        moves.forEach {
            boardSquares[it.to.position].fill = Color.FORESTGREEN
        }
    }

    /**
     * Redraw the whole board, i.e. repaint squares and also render pieces.
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
//        boardPieces[square.position.row][square.position.col].graphic = square.piece?.img
    }


    private fun getSquareColor(row: Int, col: Int): Color = getSquareColor(Position(row, col))

    private fun getSquareColor(position: Position): Color = if((position.row * 7 + position.col) % 2 < 1) Color.SANDYBROWN else Color.SADDLEBROWN

}