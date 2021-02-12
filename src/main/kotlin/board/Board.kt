package board

import game.Player
import game.theOtherPlayer
import piece.*
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
     * The previous state of the game (enables the "undo" feature)
     */
    val previousBoard: Board?

    /**
     * Initializes a new board with pieces on their initial positions and the white player
     * on turn if [setPieces] is true, or an empty board without pieces
     */
    constructor(setPieces: Boolean = true) {
        this.squares = Matrix(8, 8) { row, col ->
            val position = Position(row, col)
            if (!setPieces) {
                Square(position, null)
            } else {
                Square(position, resolvePiece(position))
            }
        }
        this.playerOnTurn = Player.WHITE
        this.previousBoard = null
    }

    /**
     * Initializes a new board as a result of updating the [previousBoard] with
     * given [squares][updatedSquaresByPosition]. If [takeTurns] is true, the players take turns.
     */
    constructor(
        previousBoard: Board,
        updatedSquaresByPosition: Map<Position, Square> = emptyMap(),
        takeTurns: Boolean = false
    ) {
        this.squares = Matrix(8, 8) { row, col ->
            val position = Position(row, col)
            updatedSquaresByPosition[position] ?: previousBoard.getSquare(position)
        }
        this.playerOnTurn = if (takeTurns) previousBoard.playerOnTurn.theOtherPlayer else previousBoard.playerOnTurn
        this.previousBoard = previousBoard
    }


    /**
     * Initializes and returns correct piece based on given [position]
     */
    private fun resolvePiece(position: Position): Piece? {
        val player: Player = if (position.row in 0..1) Player.BLACK else Player.WHITE
        return when (position.row) {
            1, 6 -> Pawn(player, position)
            0, 7 -> when(position.col) {
                0, 7 -> Rook(player, position)
                1, 6 -> Knight(player, position)
                2, 5 -> Bishop(player, position)
                3 -> Queen(player, position)
                4 -> King(player, position)
                else -> throw IllegalArgumentException("Position out of bounds")
            }
            else -> null
        }
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

    companion object {

        /**
         * An empty chess board without pieces
         */
        val EMPTY = Board(setPieces = false)

        /**
         * Chess board with pieces in initial positions
         */
        val INITIAL = Board()
    }

}

/**
 * Updates the board with given [squares] with players taking turns if [takeTurns] is true
 */
fun Board.updateWith(squares: List<Square>, takeTurns: Boolean = false) =
    Board(this, squares.associateBy { it.position }, takeTurns)
