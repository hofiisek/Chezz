import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import ui.views.ChezzAppView

/**
 * Application window
 *
 * @author Dominik Hoftych
 */
class ChezzApp : App(ChezzAppView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isResizable = false
        // how to set window size based on the contents?
        stage.height = 700.0
        stage.width = 645.0
        stage.icons.add(Image("/chess_icon.png"))
    }

}

/**
 * Main entry point to the application.
 */
fun main(args: Array<String>) {
    launch<ChezzApp>(args)
}