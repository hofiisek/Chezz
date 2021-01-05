package ui

import board.Board
import board.Position
import board.Square
import javafx.application.Platform
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.View
import tornadofx.*

/**
 * Main view of the Chess board.
 */
class ChezzView: View() {

    private val board = Board()

    override val root = gridpane {
        menubar {
            menu("File") {
                separator()
                item("Save").action {
                    println("Saving! Ehh.. not yet")
                }
                item("Quit","Shortcut+Q").action {
                    Platform.exit()
                }
            }
        }


        board.squares.map { rowSquares ->
            row {
                rowSquares.map { square ->
                    square(this, square)
                }
            }
        }

    }

    private fun square(pane: Pane, square: Square) : Rectangle {
        val isWhite: Boolean = (square.position.row * 7 + square.position.col) % 2 < 1

        return pane.rectangle {
            fill = if (isWhite) Color.SANDYBROWN else Color.SADDLEBROWN
            width = 80.0
            height = 80.0
            arcWidth = 3.0
            arcHeight = 3.0
            setOnMouseClicked { _ -> handleClick(this, square) }
        }
    }

    private fun handleClick(squareRectangle: Rectangle, square: Square) {
        println("Clicked on square: $square")
    }


}
