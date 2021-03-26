package piece

import board.Position
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import randomPiece
import randomPosition

/**
 * @author Dominik Hoftych
 */
class PiecesTest : StringSpec({

    "when a piece moves, it ends up in correct position" {
        repeat(20) {
            val piece = randomPiece().also {
                it.hasMoved shouldBe false
            }

            val position = randomPosition()
            (piece moveTo position).also {
                it.position shouldBe position
                it.player shouldBe piece.player
                it.hasMoved shouldBe true
                it.history shouldContain position
            }
        }
    }

    fun Piece.applyMoves(movesQueue: Iterator<Position>): Piece = when {
        !movesQueue.hasNext() -> this
        else -> movesQueue.next().let {
            (this moveTo it).applyMoves(movesQueue)
        }
    }

    "piece's history is correctly tracked" {
        val randomMoves = (0..20).map { randomPosition() }
        val piece = randomPiece().applyMoves(randomMoves.iterator())

        piece.position shouldBe randomMoves.last()
        piece.history.drop(1) shouldContainExactly randomMoves
    }

})
