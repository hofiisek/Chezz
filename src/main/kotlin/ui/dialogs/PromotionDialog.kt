package ui.dialogs

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import piece.*
import tornadofx.*
import ui.dialogs.PromotionDialog.PieceType.*

/**
 * TornadoFX hacks
 * see https://edvin.gitbooks.io/tornadofx-guide/content/part1/11_Editing_Models_and_Validation.html
 */
class PromotionPiece {
    val pieceProperty = SimpleObjectProperty<Piece>()
    var piece: Piece by pieceProperty
}

class PromotionPieceModel : ItemViewModel<PromotionPiece>() {
    val pieceType = bind(PromotionPiece::pieceProperty)
}


/**
 * The promotion dialog that pops up when a pawn reaches the other end of the board
 * and must be promoted
 *
 * @author Dominik Hoftych
 */
class PromotionDialog : Fragment("Promotion") {

    private val model: PromotionPieceModel by inject()

    /**
     * The pawn being promoted
     */
    val pawnToPromote: Piece by param()

    override val root: Parent = gridpane {
        row {
            values().forEach { pieceType -> add(choice(pieceType)) }
        }
    }

    /**
     * Returns a clickable square pane that, when clicked, will promote the [pawnToPromote]
     * to a piece of the given [pieceType].
     */
    private fun choice(pieceType: PieceType): Pane {
        val promotedPiece: Piece = when (pieceType) {
            QUEEN -> Queen(pawnToPromote.player, pawnToPromote.position)
            ROOK -> Rook(pawnToPromote.player, pawnToPromote.position)
            KNIGHT -> Knight(pawnToPromote.player, pawnToPromote.position)
            BISHOP -> Bishop(pawnToPromote.player, pawnToPromote.position)
        }

        return gridpane {
            val background = background()
            add(background)
            add(pieceIcon(promotedPiece))

            setOnMouseEntered { background.fill = Color.SADDLEBROWN }
            setOnMouseExited { background.fill = Color.SANDYBROWN }

            onLeftClick {
                // save and commit changes and close this dialog
                model.pieceType.value = promotedPiece
                model.commit()
                close()
            }
        }
    }

    private fun background() = Rectangle(0.0, 0.0, 120.0, 120.0).apply {
        arcWidth = 3.0
        arcHeight = 3.0
        fill = Color.SANDYBROWN
    }

    private fun pieceIcon(piece: Piece) = Label().apply {
        graphic = piece.icon
        GridPane.setHalignment(this, HPos.CENTER)
    }


    /**
     * Types of pieces to which the pawn can be promoted
     */
    private enum class PieceType {
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT
    }

}

