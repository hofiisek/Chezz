package ui.views

import game.GameResult
import game.pgnString
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.StageStyle
import tornadofx.*
import ui.controllers.BoardController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.text.html.parser.TagElement

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

    override val root =  menubar {
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

                find<TagPairsFormView>(
                    TagPairsFormView::pgn to boardController.exportPgn(),
                    TagPairsFormView::result to boardController.getGameResult()
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

class TagPairsFormView : Fragment("Tag pairs") {

    val pgn: String by param()
    val result: GameResult by param()

    private val model = ViewModel()

    // Seven Tag Roster
    private val event = model.bind { SimpleStringProperty(null, "Event") }
    private val site = model.bind { SimpleStringProperty(null, "Site")  }
    private val date = model.bind { SimpleObjectProperty<LocalDate>(null, "Date")  }
    private val round = model.bind { SimpleStringProperty(null, "Round")  }
    private val white = model.bind { SimpleStringProperty(null, "White")  }
    private val black = model.bind { SimpleStringProperty(null, "Black")  }

    override val root: Parent = form {
        fieldset {
            field("Event") { textfield(event).required() }
            field("Site") { textfield(site).required() }
            field("Date") { datepicker(date).required() }
            field("Round") { textfield(round).required() }
            field("White") { textfield(white).required() }
            field("Black") { textfield(black).required() }

            // TODO center the button + sort properties
            button("Done").enableWhen(model.valid).action {
                chooseFile(
                    title = "Select a PGN file to import",
                    filters = arrayOf(ExtensionFilter(".pgn files", "*.pgn")),
                    mode = FileChooserMode.Save
                ).firstOrNull()?.let { file ->
                    file.bufferedWriter().use {
                        model.propertyMap.forEach { (t, _) ->
                            it.write(t.value.asTagPair(t.name))
                            it.newLine()
                        }
                        it.write(result.pgnString().asTagPair("Result"))


                        it.newLine()
                        it.newLine()
                        it.write(pgn)
                    }
                    close()
                }
            }

        }
    }

    private fun Any.asTagPair(tag: String): String {
        val str = when (this) {
            is LocalDate -> this.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            else -> this
        }
        return """[$tag "$str"]"""
    }


}