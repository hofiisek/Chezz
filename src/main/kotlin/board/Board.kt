package board

import game.Player
import piece.*
import java.lang.IllegalStateException

class Board {

    val tiles: List<List<Tile>>

    constructor() {
        this.tiles = (0..7).map { row ->
            (0..7).map { col ->
                Tile(row, col).apply {
                    piece = getPiece(this, if (row in 0..2) Player.BLACK else Player.WHITE)
                }
            }.toList()

        }
    }

    constructor(tiles: List<List<Tile>>) {
        this.tiles = tiles
    }

    constructor(other: Board) {
        this.tiles = other.tiles
    }

    private fun getPiece(tile: Tile, player: Player): Piece? = when(tile.rank) {
        2,7 -> Pawn(player, tile)
        in 3..6 -> null
        else -> when(tile.file) {
            'a' -> Rook(player, tile)
            'b' -> Knight(player, tile)
            'c' -> Bishop(player, tile)
            'd' -> King(player, tile)
            'e' -> Queen(player, tile)
            'f' -> Bishop(player, tile)
            'g' -> Knight(player, tile)
            'h' -> Rook(player, tile)
            else -> throw IllegalStateException("Tile out of bounds")
        }
    }

    fun print() {
//        tiles.forEach { row ->
//            row.forEach { tile -> print(" ${tile.file}${tile.rank} ") }
//            println()
//        }

        tiles.forEach { row ->
            row.forEach { tile -> print(tile.piece?.unicode() ?: " ") }
            println()
        }
    }

}