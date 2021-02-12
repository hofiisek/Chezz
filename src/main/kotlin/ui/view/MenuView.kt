package ui.view

import javafx.application.Platform
import tornadofx.*
import ui.controller.BoardController
import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty

/**
 * Menu view
 *
 * @author Dominik Hoftych
 */
class MenuView : View() {

    private val view: BoardView by inject()
    private val controller: BoardController by inject()

    override val root =  menubar {
        menu("Play") {
            item("New game").action {
                view.startGame()
            }
            item("Load game").action {
                // TODO
            }
        }
        menu("State") {
            item("Save game").action {
                // TODO
            }
            item("Undo last move") {
                action {
                    if (controller.isGameStarted()) view.undoLastMove()
                }
            }
            item("Quit").action {
                Platform.exit()
            }
        }
    }

}