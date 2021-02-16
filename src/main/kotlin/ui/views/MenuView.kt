package ui.views

import javafx.application.Platform
import tornadofx.*
import ui.controllers.BoardController

/**
 * Menu view
 *
 * @author Dominik Hoftych
 */
class MenuView : View() {

    /**
     * Reference to the board controller in which the menu actions are handled
     */
    private val boardController: BoardController by inject()

    override val root =  menubar {
        menu("Play") {
            item("New game").action {
                boardController.startGame()
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
                    boardController.undoLastMove()
                }
            }
            item("Quit").action {
                Platform.exit()
            }
        }
    }

}