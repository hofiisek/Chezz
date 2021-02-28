package board

import game.Move
import game.Player
import game.perform
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
     * History of played moves
     */
    val playedMoves: List<Move>

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
        this.playedMoves = emptyList()
    }

    /**
     * Initializes a new board as a result of updating the [previousBoard] with
     * given [squares][updatedSquaresByPosition]. If [takeTurns] is true, the players take turns.
     */
    constructor(
        previousBoard: Board,
        updatedSquaresByPosition: Map<Position, Square> = emptyMap(),
        takeTurns: Boolean = false,
        playedMoves: List<Move>
    ) {
        this.squares = Matrix(8, 8) { row, col ->
            val position = Position(row, col)
            updatedSquaresByPosition[position] ?: previousBoard.getSquare(position)
        }
        this.playerOnTurn = if (takeTurns) previousBoard.playerOnTurn.theOtherPlayer else previousBoard.playerOnTurn
        this.previousBoard = previousBoard
        this.playedMoves = playedMoves
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
        return if (position.isOnBoard) squares[position] else throw IllegalArgumentException("Position not on board")
    }

    /**
     * Returns the [Square] on given [position], or null if the given position is not on the board
     */
    fun getSquareOrNull(position: Position): Square? {
        return if (position.isOnBoard) squares[position] else null
    }

    /**
     * Returns all pieces of given [player] and [type]
     */
    fun <T: Piece> getPiecesFor(player: Player = playerOnTurn, type: KClass<T>): List<T> {
        return squares
                .mapNotNull { it.piece }
                .filter { it.player == player }
                .filterIsInstance(type.java)
                .map { type.cast(it) }
    }

    /**
     * Returns all pieces for given [player]
     */
    fun getPiecesFor(player: Player = playerOnTurn): List<Piece> {
        return squares
                .mapNotNull { it.piece }
                .filter { it.player == player }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (squares != other.squares) return false
        if (playerOnTurn != other.playerOnTurn) return false
        if (previousBoard != other.previousBoard) return false
        if (playedMoves != other.playedMoves) return false

        return true
    }

    override fun hashCode(): Int {
        var result = squares.hashCode()
        result = 31 * result + playerOnTurn.hashCode()
        result = 31 * result + (previousBoard?.hashCode() ?: 0)
        result = 31 * result + playedMoves.hashCode()
        return result
    }


    companion object {

        /**
         * Returns a new empty chess board without pieces
         */
        fun emptyBoard() = Board(setPieces = false)

        /**
         * Returns a new chess board with pieces in initial positions
         */
        fun initialBoard() = Board()
    }

}

/**
 * Returns true if the white player is on turn.
 * Convenience method to avoid the long and ugly equality checks everytime.
 */
fun Board.whiteOnTurn(): Boolean = playerOnTurn == Player.WHITE

/**
 * Plays the given [move] and returns an updated board with the move recorded
 * in the list of played moves. If [takeTurns] is true, the players take turn.
 */
fun Board.playMove(move: Move, takeTurns: Boolean = true): Board = Board(
    previousBoard = this,
    updatedSquaresByPosition = move.perform().associateBy { it.position },
    takeTurns = takeTurns,
    playedMoves = playedMoves.plus(move)
)

/**
 * Simulates the given [move] and returns an updated board. Contrary to the [playMove] method,
 * the players do not take turns - the move is only simulated to obtain the board state if
 * the move was played
 */
fun Board.simulateMove(move: Move): Board = playMove(move = move, takeTurns = false)
