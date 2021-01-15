package board

import game.Player
import piece.*
import tornadofx.getProperty
import java.lang.IllegalStateException

/**
 * Chess board with a 8x8 array of [Square]s holding the current game state.
 *
 * @param squares
 *
 * @author Dominik Hoftych
 */
class Board(val squares: List<List<Square>>) {

    /**
     * Default constructor to be called when initializing an empty board
     */
    constructor() : this(
        List(8) { row ->
            List(8) { col ->
                Square(row, col)
            }
        }
    )

    /**
     * Set pieces on the board
     */
    init {
        for (row in squares.take(2)) {
            row.forEach { it.apply { piece = resolvePiece(this, Player.WHITE) } }
        }
        for (row in squares.takeLast(2)) {
            row.forEach { it.apply { piece = resolvePiece(this, Player.BLACK) } }
        }
    }

    /**
     * Returns the correct piece to be set on given [Square], or null if the [Square] should be empty
     */
    private fun resolvePiece(square: Square, player: Player): Piece? = when(square.rank) {
        2,7 -> Pawn(player, square.position)
        in 3..6 -> null
        else -> when(square.file) {
            'a' -> Rook(player, square.position)
            'b' -> Knight(player, square.position)
            'c' -> Bishop(player, square.position)
            'd' -> King(player, square.position)
            'e' -> Queen(player, square.position)
            'f' -> Bishop(player, square.position)
            'g' -> Knight(player, square.position)
            'h' -> Rook(player, square.position)
            else -> throw IllegalStateException("Tile out of bounds")
        }
    }

    /**
     * Returns the [Square] based on given [Position], or throw exception if given position is not on the board
     */
    fun getSquare(position: Position): Square {
        return if (position.onBoard) squares[position.row][position.col] else throw IllegalStateException("Position not on board")
    }

    /**
     * Returns the [Square] based on given [Position], or null if the given position is not on the board
     */
    fun getSquareOrNull(position: Position): Square? {
        return if (position.onBoard) squares[position.row][position.col] else null
    }

    /**
     * Returns the piece on the [Square] with given [Position], or null if there's no piece
     */
    fun getPiece(position: Position): Piece? = squares[position.row][position.col].piece

    /**
     * Print the board with pieces as unicode symbols
     */
    fun printUnicode() {
        squares.forEach { row ->
            row.forEach { tile -> print(tile.piece?.unicode() ?: " ") }
            println()
        }
    }

}