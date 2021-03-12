package ui.fragments

import game.GameResult
import game.asString
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.stage.FileChooser
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A form with 7 tags to be filled and a button that actually saves the game
 * and form content to a .pgn file.
 * These tags are mandatory and are known as the Seven Tag Roster.
 * Optional tags are omitted.
 */
class SaveGameDialog : Fragment("Tag pairs") {

    val movetext: String by param()
    val result: GameResult by param()

    private val model = ViewModel()

    // Seven Tag Roster
    private val event = model.bind { SimpleStringProperty(null, "Event", "ChessNoobs 2021") }
    private val site = model.bind { SimpleStringProperty(null, "Site", "Prague, CZE")  }
    private val date = model.bind { SimpleObjectProperty(null, "Date", LocalDate.now())  }
    private val round = model.bind { SimpleStringProperty(null, "Round", "1")  }
    private val white = model.bind { SimpleStringProperty(null, "White", "You")  }
    private val black = model.bind { SimpleStringProperty(null, "Black", "Also you?")  }

    override val root: Parent = form {
        fieldset {
            field("Event") { textfield(event).required() }
            field("Site") { textfield(site).required() }
            field("Date") { datepicker(date).required() }
            field("Round") { textfield(round).required() }
            field("White") { textfield(white).required() }
            field("Black") { textfield(black).required() }
        }

        button("Save").enableWhen(model.valid).action {
            chooseFile(
                title = "Select a PGN file to import",
                filters = arrayOf(FileChooser.ExtensionFilter(".pgn files", "*.pgn")),
                mode = FileChooserMode.Save
            ).firstOrNull()?.let { file ->
                file.bufferedWriter().use {
                    listOf(event, site, date, round, white, black).forEach { property ->
                        it.write(property.get().asTagPair(property.name))
                        it.newLine()
                    }
                    it.write(result.asTagPair("Result"))
                    it.newLine()
                    it.newLine()
                    it.write(movetext)
                }
                close()
            }
        }
    }

    private fun Any.asTagPair(tag: String): String {
        val str = when (this) {
            is LocalDate -> this.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            is GameResult -> this.asString()
            else -> this
        }
        return """[$tag "$str"]"""
    }


}