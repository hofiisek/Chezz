package ui.fragments

import game.GameResult
import game.WinType
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.text.FontWeight
import tornadofx.*
import ui.controllers.BoardController

/**
 * Dialog window that pops up when the game ends
 *
 * @author Dominik Hoftych
 */
class GameOverDialog : Fragment("Game over") {

    /**
     * The result of the game
     */
    val gameResult: GameResult by param()

    /**
     * Reference to board controller in which the button actions are handled
     */
    private val boardController: BoardController by inject()

    override val root: Parent = gridpane {
        row { resultText(this) }
        row { buttons(this) }
    }

    /**
     * Displays text with the result of the game in the given [pane]
     */
    private fun resultText(pane: Pane) {
        pane.label(
            """
            The game is over.. ${when (val result = gameResult) {
                is GameResult.BlackWins -> {
                    "BLACK player wins ${if (result.type == WinType.CHECKMATE) "by checkmate" else "on time"}!"
                }
                is GameResult.WhiteWins -> {
                    "WHITE player wins ${if (result.type == WinType.CHECKMATE) "by checkmate" else "on time"}!"
                }
                is GameResult.Draw -> {
                    "DRAW due to ${result.type}"
                }
                GameResult.StillPlaying -> throw IllegalArgumentException("Result must be known at this point")
            }}
            """.trimIndent()
        ) {
            style {
                fontWeight = FontWeight.BOLD
                alignment = Pos.CENTER
                paddingAll = 10
            }
        }
    }

    /**
     * Adds "menu" buttons to the given [pane]
     */
    private fun buttons(pane: Pane) {
        pane.hbox(spacing = 5) {
            button("Play another one").action {
                boardController.startGame()
                close()
            }
            button("Save game").action {
                find<SaveGameDialog>(
                    SaveGameDialog::movetext to boardController.exportPgn(),
                    SaveGameDialog::result to boardController.getGameResult()
                ).openModal(resizable = false)
                close()
            }
            button("Convince yourself").action { close() }
            button("Get life and quit").action { Platform.exit() }
            style {
                alignment = Pos.CENTER
                paddingAll = 10
            }
        }
    }
}
