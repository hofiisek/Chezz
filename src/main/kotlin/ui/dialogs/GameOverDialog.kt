package ui.dialogs

import game.GameResult
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
    val result: GameResult by param()

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
            The game ended, ${when (result) {
                is GameResult.Checkmate -> "${result.winningPlayer} player wins!"
                is GameResult.WinOnTime -> "${result.winningPlayer} player wins on time!"
                is GameResult.FiftyMoveRule -> "as a draw due to the fifty-move rule!"
                is GameResult.Stalemate -> "as a draw due to stalemate!"
                is GameResult.ThreefoldRepetition -> "as a draw due to the threefold repetition rule!"
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
        pane.hbox {
            button("Play another one").action {
                boardController.startGame()
                close()
            }
            button("Save game").action {
                "TODO"
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