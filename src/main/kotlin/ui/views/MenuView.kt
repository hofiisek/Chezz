package ui.views

import javafx.application.Platform
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.*
import ui.controllers.BoardController
import java.io.File

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
                chooseFile(
                    title = "Select PGN file to import",
                    filters = arrayOf(ExtensionFilter(".pgn files", "*.pgn"))
                ).firstOrNull()?.let { boardController.importPgn(it) }
            }

            item("Quit", "ShortCut+Q").action {
                Platform.exit()
            }
        }

        menu("State") {
            item("Save game", "Shortcut+S").action {
                // TODO show export options - PGN headers form
                val pgn: String = boardController.exportPgn()
                val result = chooseDirectory(title = "Select the directory to which to save the file") {  }
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