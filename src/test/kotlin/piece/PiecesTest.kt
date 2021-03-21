package piece

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

            (piece moveTo position).let {
                it.position shouldBe position
                it.player shouldBe piece.player
                it.hasMoved shouldBe true
                it.history shouldContain position
            }
        }
    }

    "piece's history is correctly tracked" {
        val randomPositions = (0..20).map { randomPosition() }

        // unfortunately need to mutate the piece here
        var piece = randomPiece()
        randomPositions.forEach { piece = piece moveTo it }

        piece.position shouldBe randomPositions.last()
        piece.history.drop(1) shouldContainExactly randomPositions
    }
})
