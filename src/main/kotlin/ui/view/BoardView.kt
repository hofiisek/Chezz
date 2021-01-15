package ui.view

import board.Board
import board.Position
import board.Square
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
    private val boardSquares: List<List<Rectangle>> = List(8) {
        List(8) {
            Rectangle(0.0, 0.0, 80.0, 80.0).apply { arcWidth = 3.0; arcHeight = 3.0 }
        }
    }

    /**
     * Matrix of [Label]s that contain the piece images.
     */
    private val boardPieces: List<List<Label>> = List(8) {
        List(8) {
            Label().apply {
                GridPane.setHalignment(this, HPos.CENTER)
            }
        }
    }

    override val root = gridpane {
        for (row in controller.getSquares()) {
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
            val (row, col) = square.position
            add(boardSquares[row][col])
            add(boardPieces[row][col])

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
     * If there are no such moves, repaint the board squares to default colors
     */
    fun renderAllowedMoves(moves: Set<Move>) {
        if (moves.isNotEmpty()) {
            moves.map { it.to.position }.forEach { (row, col) ->
                boardSquares[row][col].fill = Color.FORESTGREEN
            }
        } else {
            repaintBoard()
        }

        // :((
//        moves.takeIf { it.isNotEmpty() }
//                ?.map { it.to.position }
//                ?.forEach { (row, col) -> boardSquares[row][col].fill = Color.FORESTGREEN }
//                ?: repaintBoard()
    }

    /**
     * Redraw the whole board, i.e. repaint squares and also render pieces.
     */
    fun redrawBoard(board: Board) {
        repaintBoard()
        board.squares.flatten().forEach { square ->
            redrawPiece(square)
        }
    }

    /**
     * Paints the board squares to default colors
     */
    fun repaintBoard() {
        for ((row, rowSquares) in boardSquares.withIndex()) {
            for ((col, square) in rowSquares.withIndex()) {
                square.fill = getSquareColor(row, col)
            }
        }
    }

    /**
     * Renders the piece on given [Square] if there is any, otherwise removes
     * the piece image from given square.
     */
    private fun redrawPiece(square: Square) {
        boardPieces[square.position.row][square.position.col].graphic = square.piece?.img
    }


    private fun getSquareColor(row: Int, col: Int): Color = getSquareColor(Position(row, col))

    private fun getSquareColor(position: Position): Color = if((position.row * 7 + position.col) % 2 < 1) Color.SANDYBROWN else Color.SADDLEBROWN

}