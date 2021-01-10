package ui.view

import board.Square
import game.Move
import javafx.application.Platform
import javafx.geometry.HPos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import piece.Piece
import tornadofx.View
import tornadofx.*
import ui.controller.ChezzController


/**
 * Main view of the Chess board.
 */
class ChezzView : View("Chezz") {

    private val controller: ChezzController by inject()

    private val boardSquares: List<List<Rectangle>> = List(8) {
        List(8) {
            Rectangle(0.0, 0.0, 80.0, 80.0).apply { arcWidth = 3.0; arcHeight = 3.0 }
        }
    }

    override val root = borderpane {
        menu(this)
        board(this)
        timer(this)
    }

    fun renderSelectedPiece(selectedPiece: Piece) {
        val (row, col) = selectedPiece.square.position
        boardSquares[row][col].fill = Color.OLIVE
    }

    fun renderAllowedMoves(moves: Set<Move>) {
        if (moves.isEmpty()) {
            repaintBoard()
            return
        }

        moves.map { it.to.position }.forEach { (row, col) ->
            boardSquares[row][col].fill = Color.FORESTGREEN
        }
    }

    fun renderMove(move: Move) {

    }

    /**
     * Paints the squares with corresponding colors
     */
    fun repaintBoard() {
        for ((row, rowSquares) in boardSquares.withIndex()) {
            for ((col, square) in rowSquares.withIndex()) {
                square.fill = getSquareColor(row, col)
            }
        }
    }

    private fun menu(pane: BorderPane) {
        return pane.top {
            menubar {
                menu("File") {
                    item("Save", "Shortcut+S").action {
                        println("Saving! Ehh.. not yet")
                    }
                    item("Quit","Shortcut+Q").action {
                        Platform.exit()
                    }
                }
            }
        }
    }

    private fun board(pane: BorderPane) {
        pane.center {
            gridpane {
                controller.getSquares().map { rowSquares ->
                    row {
                        rowSquares.map { square ->
                            initSquareUI(this, square)
                        }
                    }
                }
            }
            onRightClick {
                controller.resetSelection()
            }
        }
        repaintBoard()
    }

    private fun timer(pane: BorderPane) {
        pane.right {
            vbox {

            }
        }
    }

    private fun initSquareUI(pane: Pane, square: Square) : Pane {
        // could not center the piece image other than using another nested gridpane :/
        return pane.gridpane {

            add(boardSquares[square.position.row][square.position.col])

            label {
                graphic = square.getPieceImg()
                GridPane.setHalignment(this, HPos.CENTER)
            }

            onLeftClick {
                controller.onSquareClicked(square)
            }
        }
    }

    private fun getSquareColor(row: Int, col: Int): Color = if((row * 7 + col) % 2 < 1) Color.SANDYBROWN else Color.SADDLEBROWN


}

