package ui

import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import ui.view.ChezzAppView

class ChezzApp : App(ChezzAppView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isResizable = false
        stage.height = 800.0
        stage.width = 1000.0
    }

}

/**
 * Main entry point to the application.
 */
fun main(args: Array<String>) {
    launch<ChezzApp>(args)
}