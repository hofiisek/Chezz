package piece

import board.Tile
import game.Player

sealed class Piece {
    abstract val position: Tile
    abstract val player: Player

    abstract fun getMoves()

    fun unicode(): String = when(this) {
        is Pawn -> if (player == Player.WHITE) "\u2659" else "\u265F"
        is Rook -> if (player == Player.WHITE) "\u2656" else "\u265C"
        is Knight -> if (player == Player.WHITE) "\u2658" else "\u265E"
        is Bishop -> if (player == Player.WHITE) "\u2657" else "\u265D"
        is Queen -> if (player == Player.WHITE) "\u2655" else "\u265B"
        is King -> if (player == Player.WHITE) "\u2654" else "\u265A"
    }
}

data class Pawn(override val player: Player, override val position: Tile) : Piece() {

    override fun getMoves() {

    }
}

data class Rook(override val player: Player, override val position: Tile) : Piece() {

    override fun getMoves() {

    }
}

data class Knight(override val player: Player, override val position: Tile) : Piece() {

    override fun getMoves() {

    }
}

data class Bishop(override val player: Player, override val position: Tile) : Piece() {

    override fun getMoves() {

    }
}

data class Queen(override val player: Player, override val position: Tile) : Piece() {


    override fun getMoves() {

    }
}

data class King(override val player: Player, override val position: Tile) : Piece() {

    override fun getMoves() {

    }
}
