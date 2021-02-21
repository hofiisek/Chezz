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
            item("New game", "Shortcut+N").action {
                boardController.startGame()
            }
            item("Load game", "Shortcut+L").action {
                // TODO
            }
            item("Quit", "ShortCut+Q").action {
                Platform.exit()
            }
        }
        menu("State") {
            item("Save game", "Shortcut+S").action {
                // TODO show export options - PGN headers form
                boardController.exportToPgn()
            }
            item("Undo last move", "Shortcut+Z").action {
                boardController.undoLastMove()
            }
        }
        menu("Settings") {
            // TODO - show check, show allowed moves, ..
        }
    }

}