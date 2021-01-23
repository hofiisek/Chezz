package board

import game.Player
import piece.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.reflect.KClass

/**
 * Chess board with a 8x8 array of [Square]s holding the current game state.
 *
 * @author Dominik Hoftych
 */
//TODO enable to index board using [position], i.e. board[position]
class Board {

    /**
     * A 8x8 matrix of squares
     */
    val squares: Matrix<Square>

    /**
     * Default constructor which initializes an initial board
     */
    constructor() {
        this.squares = Matrix(8, 8) { row, col ->
            val position = Position(row, col)
            val piece = when(row) {
                0 -> resolvePiece(Player.BLACK, position)
                1 -> Pawn(Player.BLACK, position)
                6 -> Pawn(Player.WHITE, position)
                7 -> resolvePiece(Player.WHITE, position)
                else -> null
            }
            Square(position, piece)
        }
    }

    /**
     * Initializes a new board with the same squares as the [other] board except for the squares
     * passed in the [squaresToUpdate] parameter.
     */
    constructor(other: Board, squaresToUpdate: List<Square>) {
        val squaresToUpdateByPosition: Map<Position, Square> = squaresToUpdate.associateBy { it.position }
        this.squares = Matrix(8, 8) { row, col ->
            val position = Position(row, col)
            squaresToUpdateByPosition.getOrDefault(position, other.getSquare(position))
        }
    }

    /**
     * Based on the column of given [position] and given [player], initializes and returns correct piece
     */
    private fun resolvePiece(player: Player, position: Position): Piece = when(position.col) {
        0 -> Rook(player, position)
        1 -> Knight(player, position)
        2 -> Bishop(player, position)
        3 -> Queen(player, position)
        4 -> King(player, position)
        5 -> Bishop(player, position)
        6 -> Knight(player, position)
        7 -> Rook(player, position)
        else -> throw IllegalStateException("Position out of bounds")
    }

    /**
     * Returns the [Square] based on given [Position], or throw exception if given position is not on the board
     */
    fun getSquare(position: Position): Square {
        return if (position.onBoard) squares[position] else throw IllegalArgumentException("Position not on board")
    }

    /**
     * Returns the [Square] based on given [Position], or null if the given position is not on the board
     */
    fun getSquareOrNull(position: Position): Square? {
        return if (position.onBoard) squares[position] else null
    }

    /**
     * Returns the [Square] currently occupied by the given [piece]
     */
    fun getSquareFor(piece: Piece): Square {
        return getSquare(piece.position)
    }

    /**
     * Returns the piece on the [Square] on given [position], or null if there's no piece
     */
    fun getPiece(position: Position): Piece? = squares[position].piece

    /**
     * Returns all pieces of given [player] and [type]
     */
    fun getPiecesFor(player: Player, type: KClass<out Piece>): List<Piece> {
        return squares.matrix.flatten()
                .mapNotNull { it.piece }
                .filter { it.player == player }
                .filter { it::class == type }
    }

}

/**
 * Print the board with pieces as unicode symbols
 */
fun Board.printUnicode() {
    squares.forEachRow { row ->
        row.forEach { square ->
            print(" ${square.piece.unicode} ")
        }
        println()
    }
}

operator fun Board.get(position: Position): Square = squares[position]
operator fun Board.get(piece: Piece): Square = squares[piece.position]
