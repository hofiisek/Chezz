import board.Board
import board.Position
import game.Player
import piece.*

/**
 * @author Dominik Hoftych
 */

fun randomPosition() = Position((0..7).random(), (0..7).random())

fun randomPositionOtherThan(other: Position): Position {
    while (true) {
        val position = randomPosition()
        if (position != other) return position
    }
}

fun randomEmptyPositionOtherThan(board: Board, other: Position): Position {
    while (true) {
        val position = randomPosition()
        if (position != other && board.getSquare(position).isUnoccupied) return position
    }
}

fun randomPiece(player: Player = listOf(Player.WHITE, Player.BLACK).random()): Piece {
    val position = randomPosition()

    return listOf(
        Pawn(player, position),
        Rook(player, position),
        Knight(player, position),
        Bishop(player, position),
        Queen(player, position),
        King(player, position)
    ).random()
}

fun String.asPosition(): Position {
    require(this.matches("""[a-h][1-8]""".toRegex()))

    return Position(
        8 - Character.getNumericValue(this.last()),
        ('a' .. 'h').indexOf(this.first())
    )
}