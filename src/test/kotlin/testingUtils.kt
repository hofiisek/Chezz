import board.Board
import board.Position
import game.Player
import piece.*

/**
 * @author Dominik Hoftych
 */

fun randomPosition() = with(0..7) {
    Position(this.random(), this.random())
}

fun randomPositionOtherThan(other: Position): Position = randomPosition().takeIf {
    it != other
} ?: randomPositionOtherThan(other)

fun Board.randomUnoccupiedPositionOtherThan(other: Position): Position = randomPosition().takeIf {
    it != other && this.getSquare(it).isUnoccupied
} ?: randomUnoccupiedPositionOtherThan(other)

fun randomPiece(player: Player = Player.values().random()): Piece = with(randomPosition()) {
    listOf(
        Pawn(player, this),
        Rook(player, this),
        Knight(player, this),
        Bishop(player, this),
        Queen(player, this),
        King(player, this)
    ).random()
}

fun String.asPosition(): Position {
    require(matches("""[a-h][1-8]""".toRegex()))

    return Position(
        8 - Character.getNumericValue(last()),
        ('a'..'h').indexOf(first())
    )
}
