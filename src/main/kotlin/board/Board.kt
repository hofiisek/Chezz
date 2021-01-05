package board

import game.Player
import piece.*
import java.lang.IllegalStateException

class Board {

    val squares: List<List<Square>>

    constructor() {
        this.squares = (0..7).map { row ->
            (0..7).map { col ->
                Square(Position(row, col)).apply {
                    piece = getPiece(this, if (row in 0..2) Player.WHITE else Player.BLACK)
                }
            }.toList()
        }
    }

    constructor(squares: List<List<Square>>) {
        this.squares = squares
    }

    constructor(other: Board) {
        this.squares = other.squares
    }

    fun getSquare(position: Position): Square = squares[position.row][position.col]

    private fun getPiece(square: Square, player: Player): Piece? = when(square.rank) {
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


    fun print() {
//        tiles.forEach { row ->
//            row.forEach { tile -> print(" ${tile.file}${tile.rank}_${tile.color}") }
//            println()
//        }

        squares.forEach { row ->
            row.forEach { tile -> print(tile.piece?.unicode() ?: " ") }
            println()
        }
    }

}