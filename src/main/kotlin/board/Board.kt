package board

import game.Player
import game.theOtherPlayer
import piece.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Chess board with a 8x8 matrix of [Square]s holding the current game state.
 *
 * @author Dominik Hoftych
 */
class Board {

    /**
     * A 8x8 matrix of squares
     */
    val squares: Matrix<Square>

    /**
     * The player who is on the turn
     */
    val playerOnTurn: Player

    /**
     * Initializes a new board with pieces on their initial positions and the white player on turn
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
        this.playerOnTurn = Player.WHITE
    }

    /**
     * Initializes a new board as a result of updating the [previous] board by given [updatedSquares].
     * If [takeTurns] is true, the players take turns.
     */
    constructor(previous: Board, updatedSquares: List<Square>, takeTurns: Boolean = false) {
        updatedSquares.associateBy { it.position }.let {
            this.squares = Matrix(8, 8) { row, col ->
                val position = Position(row, col)
                it[position] ?: previous.getSquare(position)
            }
        }
        this.playerOnTurn = if (takeTurns) previous.playerOnTurn.theOtherPlayer else previous.playerOnTurn
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
     * Returns the [Square] on given [position], or throw exception if given position is not on the board
     */
    fun getSquare(position: Position): Square {
        return if (position.onBoard) squares[position] else throw IllegalArgumentException("Position not on board")
    }

    /**
     * Returns the [Square] on given [position], or null if the given position is not on the board
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
    fun <T: Piece> getPiecesFor(player: Player, type: KClass<T>): List<T> {
        return squares
                .mapNotNull { it.piece }
                .filter { it.player == player }
                .filterIsInstance(type.java)
                .map { type.cast(it) }
    }

    /**
     * Returns all pieces for given [player]
     */
    fun getPiecesFor(player: Player): List<Piece> {
        return squares
                .mapNotNull { it.piece }
                .filter { it.player == player }
    }

}

/**
 * Updates the board with given [squares] with players taking turns if [takeTurns] is true
 */
fun Board.updateWith(squares: List<Square>, takeTurns: Boolean = false) = Board(this, squares, takeTurns)

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

/**
 * Returns the square occupied by the given [piece]
 */
operator fun Board.get(piece: Piece): Square = squares[piece.position]

/**
 * Returns the square with the given [position]
 */
operator fun Board.get(position: Position): Square = squares[position]
