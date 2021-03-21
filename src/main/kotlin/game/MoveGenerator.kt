package game

import board.*
import piece.*
import kotlin.math.abs

/**
 * The generator of valid moves
 *
 * @author Dominik Hoftych
 */
object MoveGenerator {

    /**
     * For given [piece], generates all allowed moves considering the give [board] state.
     * If [validateForCheck] is true, each move is validated to not put or leave its own king
     * in check.
     */
    fun generate(piece: Piece, board: Board, validateForCheck: Boolean = true): Set<Move> = when (piece) {
        is Pawn -> pawnMoves(piece, board) + enPassant(piece, board)
        is King -> generateMoves(board, piece, 1) + castling(piece, board, validateForCheck)
        is Knight -> generateMoves(board, piece, 1)
        is Rook -> generateMoves(board, piece, 7)
        is Bishop -> generateMoves(board, piece, 7)
        is Queen -> generateMoves(board, piece, 7)
    }.filter {
        if (validateForCheck) board.simulateMove(it).isNotCheck() else true
    }.toSet()

    /**
     * Generates the "basic" moves allowed for the given [pawn]. Such basic moves include classic advance moves
     * by 1 square forward, two square advance moves, and capture moves.
     */
    private fun pawnMoves(pawn: Pawn, board: Board): Set<Move> {
        fun Pawn.forwardBy(n: Int = 1): Position = position + (Direction(rowDirection, 0) * n)
        fun Pawn.forwardLeft(): Position = position + Direction(rowDirection, -1)
        fun Pawn.forwardRight(): Position = position + Direction(rowDirection, +1)

        val moves: MutableSet<Move> = mutableSetOf()

        // move forward by 1 square
        pawn.forwardBy().let {
            if (board.getSquareOrNull(it)?.isUnoccupied == true) {
                moves.add(BasicMove(pawn, it))
            }
        }

        // move forward by 2 squares (two-square advance)
        pawn.forwardBy(2).let { forwardByTwo ->
            val forwardByOne = pawn.forwardBy()
            when {
                pawn.hasMoved -> return@let
                listOf(forwardByOne, forwardByTwo).any { board.getSquare(it).isOccupied } -> return@let
                else -> moves.add(BasicMove(pawn, forwardByTwo))
            }
        }

        // capture moves
        listOf(pawn.forwardLeft(), pawn.forwardRight()).forEach {
            if (board.getSquareOrNull(it) occupiedBy pawn.player.theOtherPlayer) {
                moves.add(BasicMove(pawn, it, true))
            }
        }

        return moves.toSet()
    }

    /**
     * Returns a set of en passant moves for given [pawn], or an empty set if no en passant
     * moves are possible
     */
    private fun enPassant(pawn: Pawn, board: Board): Set<Move> {
        fun Pawn.backwardBy(n: Int = 1): Position = position - (Direction(rowDirection, 0) * n)
        fun Pawn.advancedTwoSquares(): Boolean = history.first() == backwardBy(2)

        return board.getPieces(pawn.theOtherPlayer, Pawn::class)
            .filter { enemyPawn ->
                if (!enemyPawn.advancedTwoSquares()) return@filter false

                val (row, col) = pawn.position
                val (enemyRow, enemyCol) = enemyPawn.position
                if (row != enemyRow || abs(col - enemyCol) != 1) return@filter false

                // TODO checking that it was the last move
                val enemyPawnPrevPosition = enemyPawn.backwardBy(2)
                board.previousBoard?.getSquare(enemyPawnPrevPosition)?.piece is Pawn
            }
            .map { EnPassantMove(pawn, it.backwardBy(), it) }
            .toSet()
    }

    /**
     * Returns a set of castling moves for given [king], or an empty set if castling is not possible
     */
    private fun castling(king: Piece, board: Board, validateForCheck: Boolean): Set<Move> {
        infix fun Piece.moveRightBy(n: Int) = Position(position.row, position.col + n)
        infix fun Piece.moveLeftBy(n: Int) = Position(position.row, position.col - n)

        // avoid infinite recursion when determining check
        if (!validateForCheck) return emptySet()

        val rooks: List<Rook> = board.getPieces(king.player, Rook::class)
            .filterNot { it.hasMoved }

        if (rooks.isEmpty() || king.hasMoved || king.isInCheck(board)) return emptySet()

        return rooks
            .filter { rook ->
                // there must be no pieces between the castling pieces
                squaresBetween(rook.position, king.position, board).none { it.isOccupied }
            }
            .filter { rook ->
                // squares crossed by the king must not be in check
                val queenSide = abs(rook.position.col - king.position.col) == 4
                val kingDestination = if (queenSide) king moveLeftBy 2 else king moveRightBy 2
                squaresBetween(king.position, kingDestination, board).none { it.isInCheck(board) }
            }
            .map { rook ->
                val queenSide: Boolean = abs(rook.position.col - king.position.col) == 4
                CastlingMove(
                    rook = if (queenSide) rook to (rook moveRightBy 3) else rook to (rook moveLeftBy 2),
                    king = if (queenSide) king to (king moveLeftBy 2) else king to (king moveRightBy 2),
                    queenSide = queenSide
                )
            }.toSet()
    }

    /**
     * Returns a list of squares that are between the given [from] and [to] positions
     */
    private fun squaresBetween(from: Position, to: Position, board: Board): List<Square> {
        require(from.row == to.row)

        val fromCol = from.col
        val toCol = to.col
        return (1 + minOf(fromCol, toCol) until maxOf(fromCol, toCol))
            .map { board.getSquare(Position(to.row, it)) }
    }


    /**
     * Recursively generates moves for given [piece] until either:
     * - an own piece is hit, or
     * - an enemy piece is hit, in such case the move is still included, or
     * - the [maxDistance] parameter is met.
     */
    private fun generateMoves(board: Board, piece: Piece, maxDistance: Int): Set<Move> {
        fun generateMovesRecursive(dir: Direction, n: Int, moves: Set<Move> = emptySet()): Set<Move> {
            if (n > maxDistance) return moves

            val newPos = piece.position + (dir * n)
            val newSquare = board.getSquareOrNull(newPos) ?: return moves

            return when {
                newSquare occupiedBy piece.player -> moves
                newSquare occupiedBy piece.theOtherPlayer -> moves.plus(BasicMove(piece, newPos, true))
                else -> generateMovesRecursive(dir, n + 1, moves.plus(BasicMove(piece, newPos)))
            }
        }

        return piece.movement.flatMap { generateMovesRecursive(it, 1) }.toSet()
    }

}

/**
 * Direction along x and y axis respectively
 */
typealias Direction = Pair<Int, Int>

private operator fun Direction.times(n: Int) = Direction(n * this.first, n * this.second)
