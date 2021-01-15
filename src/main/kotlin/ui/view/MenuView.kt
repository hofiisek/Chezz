package ui.view

import javafx.application.Platform
import tornadofx.*

/**
 * Menu view
 *
 * @author Dominik Hoftych
 */
class MenuView : View() {

    override val root =  menubar {
        menu("File") {
            item("Save", "Shortcut+S").action {
                println("Saving! Ehh.. not yet")
            }
            item("Load", "Shortcut+L").action {
                println("Loading!....")
            }
            item("Quit","Shortcut+Q").action {
                Platform.exit()
            }
        }
    }

}