import board.*
import game.Player
import piece.Piece


fun main(args: Array<String>)  {
    val sq = Matrix<Square>(8, 8) { row, col->
        Square(row, col)
    }

    val b = Board()
    b.printUnicode()

}