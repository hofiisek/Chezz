package board

import game.Player
import piece.*
import tornadofx.getProperty
import java.lang.IllegalStateException

/**
 * Chess board with a 8x8 array of [Square]s holding the current game state.
 */
class Board(val squares: List<List<Square>>) {

    /**
     * Default constructor called only once at the beginning of a new game
     */
    constructor() : this(
        List(8) { row ->
            List(8) { col ->
                Square(row, col)
            }
        }
    )

    init {
        this.squares.flatten().forEach {
            it.apply {
                this.piece = resolvePiece(this, if (position.row in 0..2) Player.WHITE else Player.BLACK)
            }
        }
    }

    constructor(other: Board) : this(other.squares)


    private fun resolvePiece(square: Square, player: Player): Piece? = when(square.rank) {
        2,7 -> Pawn(player, square)
        in 3..6 -> null
        else -> when(square.file) {
            'a' -> Rook(player, square)
            'b' -> Knight(player, square)
            'c' -> Bishop(player, square)
            'd' -> King(player, square)
            'e' -> Queen(player, square)
            'f' -> Bishop(player, square)
            'g' -> Knight(player, square)
            'h' -> Rook(player, square)
            else -> throw IllegalStateException("Tile out of bounds")
        }
    }

    fun getSquare(position: Position): Square = squares[position.row][position.col]

    fun getSquareSafe(position: Position): Square? = if (position.onBoard) squares[position.row][position.col] else null

    fun getPiece(position: Position): Piece? = squares[position.row][position.col].piece

    fun printUnicode() {
        squares.forEach { row ->
            row.forEach { tile -> print(tile.piece?.unicode() ?: " ") }
            println()
        }
    }

}