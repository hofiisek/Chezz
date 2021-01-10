package ui

import board.Board
import board.Square
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
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
//        menubar {
//            menu("File") {
//                separator()
//                item("Save").action {
//                    println("Saving! Ehh.. not yet")
//                }
//                item("Quit","Shortcut+Q").action {
//                    Platform.exit()
//                }
//            }
//        }
//        imageview {
//            image = Image("/pieces/b_king.png", 40.0, 40.0, true, true)
//        }

//        row {
//            pane {
//                rectangle {
//                    fill = if (true) Color.SANDYBROWN else Color.SADDLEBROWN
//                    width = 80.0
//                    height = 80.0
//                    arcWidth = 3.0
//                    arcHeight = 3.0
//                }
//                label {
//                    graphic =  ImageView(Image("/pieces/b_king.png", 40.0, 40.0, true, true))
//
//                }
//            }
//        }


        board.squares.map { rowSquares ->
            row {
                rowSquares.map { square ->
                    square(this, square)
                }
            }
        }

    }

    private fun square(pane: Pane, square: Square) : Pane {
        val isWhite: Boolean = (square.position.row * 7 + square.position.col) % 2 < 1

        return pane.pane {
            rectangle {
                fill = if (isWhite) Color.SANDYBROWN else Color.SADDLEBROWN
                width = 80.0
                height = 80.0
                arcWidth = 3.0
                arcHeight = 3.0
            }

            imageview {
                Image("/pieces/b_king.png", 40.0, 40.0, true, true)
            }
//            label {
//                graphic =  ImageView(Image("/pieces/b_king.png", 40.0, 40.0, true, true))
//                alignment = Pos.BOTTOM_RIGHT
//            }
            setOnMouseClicked { _ -> handleClick(square) }
        }
    }

    private fun handleClick(square: Square) {
        println("Clicked on square: $square")

    }


}
