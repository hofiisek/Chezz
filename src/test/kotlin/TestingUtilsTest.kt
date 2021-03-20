import board.Board
import board.Position
import board.Square
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * @author Dominik Hoftych
 */
class TestingUtilsTest : StringSpec({

    "either a valid position is created or an exception" +
        " is thrown when creating position from string" {
            val ranks = listOf(1, 2, 3, 4, 5, 6, 7, 8)
            val files = ('a'..'h')

            ranks.shuffled().zip(files.shuffled()).forEach { (rank, file) ->
                "$file$rank".asPosition().let {
                    it shouldBe Position(8 - rank, files.indexOf(file))
                    it.rank shouldBe rank
                    it.file shouldBe file
                }
            }

            val invalidFiles = ('i'..'z')
            val invalidRanks = (8..20).plus((-10 until 0))
            repeat(20) {
                shouldThrowExactly<IllegalArgumentException> {
                    "${invalidFiles.random()}${invalidRanks.random()}".asPosition()
                }
            }
        }

    "getting a random position returns a position other that the given" {
        repeat(20) {
            randomPosition().let { position ->
                randomPositionOtherThan(position) shouldNotBe position
            }
        }
    }

    "getting a random empty position returns an unoccupied position other than the given" {
        val board = Board.initialBoard()
        repeat(20) {
            randomPosition().let { position ->
                val generatedPosition = randomEmptyPositionOtherThan(board, position)
                generatedPosition shouldNotBe position
                board.getSquare(generatedPosition) shouldBe unoccupied()
            }
        }
    }
})

fun unoccupied() = object : Matcher<Square> {
    override fun test(value: Square) = MatcherResult(
        value.isUnoccupied,
        "Square $value should be unoccupied",
        "Square $value should be occupied"
    )
}
