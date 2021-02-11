package ui.view

import javafx.application.Platform
import tornadofx.*

/**
 * Menu view
 *
 * @author Dominik Hoftych
 */
class MenuView : View() {

    private val view: BoardView by inject()

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
            item("Undo last move").action {
                // TODO
            }
            item("Quit").action {
                Platform.exit()
            }
        }
    }

}