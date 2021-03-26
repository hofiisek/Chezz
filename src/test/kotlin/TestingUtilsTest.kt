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

    "when creating position from string, either a valid position is created or an exception is thrown " {
            val ranks = (1..8).shuffled()
            val files = ('a'..'h')

            ranks.zip(files.shuffled()).forEach { (rank, file) ->
                "$file$rank".asPosition().let {
                    it.rank shouldBe rank
                    it.file shouldBe file
                    it shouldBe Position(8 - rank, files.indexOf(file))
                }
            }

            val invalidFiles = ('i'..'z')
            val invalidRanks = (8..20) + (-10..0)
            repeat(20) {
                shouldThrowExactly<IllegalArgumentException> {
                    "${invalidFiles.random()}${invalidRanks.random()}".asPosition()
                }
            }
        }

    "getting a random position returns a position other that the given" {
        repeat(20) {
            randomPosition().also {
                randomPositionOtherThan(it) shouldNotBe it
            }
        }
    }

    "getting a random unoccupied position returns an unoccupied position other than the given" {
        with(Board.initialBoard()) {
            repeat(20) {
                randomPosition().let { position ->
                    randomUnoccupiedPositionOtherThan(position).let { randomPosition ->
                        randomPosition shouldNotBe position
                        getSquare(randomPosition) shouldBe unoccupied()
                    }
                }
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
