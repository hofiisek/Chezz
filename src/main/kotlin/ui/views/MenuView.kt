package ui.views

import javafx.application.Platform
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.*
import ui.controllers.BoardController
import ui.fragments.SaveGameDialog
import java.util.*

/**
 * Navigation menu bar with shortcuts
 *
 * @author Dominik Hoftych
 */
class MenuView : View() {

    /**
     * Reference to the board controller in which the menu actions are handled
     */
    private val boardController: BoardController by inject()

    override val root = menubar {
        menu("Play") {
            item("New game", "Shortcut+N").action {
                boardController.startGame()
            }
            item("Load game", "Shortcut+L").action {
                chooseFile(
                    title = "Select a PGN file to import",
                    filters = arrayOf(ExtensionFilter(".pgn files", "*.pgn"))
                ).firstOrNull()?.let { boardController.importPgn(it) }
            }
            item("Quit", "ShortCut+Q").action {
                Platform.exit()
            }
        }

        menu("State") {
            item("Save game", "Shortcut+S").action {
                if (!boardController.hasGameStarted()) return@action

                find<SaveGameDialog>(
                    SaveGameDialog::movetext to boardController.exportPgn(),
                    SaveGameDialog::result to boardController.getGameResult()
                ).openModal(resizable = false)
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
