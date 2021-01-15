package ui.view

import tornadofx.View
import tornadofx.*

/**
 * Main view of the Chezz application, grouping together other views in a borderpane
 */
class ChezzAppView : View("Chezz") {

    override val root = borderpane {
        top<MenuView>()
        center<BoardView>()
        right<TimerView>()
    }

}

