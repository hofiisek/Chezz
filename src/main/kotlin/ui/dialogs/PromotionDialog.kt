package ui.dialogs

import javafx.geometry.HPos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import piece.*
import tornadofx.*
import ui.controllers.BoardController

/**
 * The promotion dialog that pops up when a pawn reaches the other end of the board
 * and must be promoted
 *
 * @author Dominik Hoftych
 */
class PromotionDialog : Fragment("Promotion") {

    /**
     * The pawn being promoted
     */
    val pawnToPromote: Pawn by param()

    /**
     * Reference to the board controller which handles the promotion
     */
    private val boardController: BoardController by inject()

    override val root: Parent = gridpane {
        row {
            PromotionPieceType.values().forEach { pieceType -> add(choice(pieceType)) }
        }
    }

    /**
     * Returns a clickable square pane that selects the piece
     * to which the pawn is promoted based on given [pieceType].
     */
    private fun choice(pieceType: PromotionPieceType): Pane {
        val promotionPiece: Piece = when (pieceType) {
            PromotionPieceType.QUEEN -> Queen(pawnToPromote.player, pawnToPromote.position)
            PromotionPieceType.ROOK -> Rook(pawnToPromote.player, pawnToPromote.position)
            PromotionPieceType.KNIGHT -> Knight(pawnToPromote.player, pawnToPromote.position)
            PromotionPieceType.BISHOP -> Bishop(pawnToPromote.player, pawnToPromote.position)
        }

        return gridpane {
            val background = background()
            add(background)
            add(pieceIcon(promotionPiece))

            setOnMouseEntered { background.fill = Color.SADDLEBROWN }
            setOnMouseExited { background.fill = Color.SANDYBROWN }

            onLeftClick {
                boardController.promotePawn(promotionPiece)
                close()
            }
        }
    }

    /**
     * Returns a rectangle with sandy brown background
     */
    private fun background() = Rectangle(0.0, 0.0, 120.0, 120.0).apply {
        arcWidth = 3.0
        arcHeight = 3.0
        fill = Color.SANDYBROWN
    }

    /**
     * Returns a label with an icon of given [piece]
     */
    private fun pieceIcon(piece: Piece) = Label().apply {
        graphic = piece.icon
        GridPane.setHalignment(this, HPos.CENTER)
    }

    /**
     * Types of pieces to which the pawn can be promoted
     */
    private enum class PromotionPieceType {
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT
    }
}
