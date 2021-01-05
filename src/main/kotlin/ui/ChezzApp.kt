package ui

import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class ChezzApp : App(ChezzView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isResizable = false
        stage.height = 800.0
        stage.width = 1000.0
    }

}


fun main(args: Array<String>) {
    launch<ChezzApp>(args)
}