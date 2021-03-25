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
        fun Pawn.forwardBy(n: Int): Position = position + (Direction(rowDirection, 0) * n)
        fun Pawn.forwardLeft(): Position = position + Direction(rowDirection, -1)
        fun Pawn.forwardRight(): Position = position + Direction(rowDirection, +1)

        val forwardByOne = pawn.forwardBy(1)
        val forwardByTwo = pawn.forwardBy(2)
        val advanceMoves = if (board.getSquare(forwardByOne).isUnoccupied) {
            if (!pawn.hasMoved && board.getSquare(forwardByTwo).isUnoccupied) {
                listOf(forwardByOne, forwardByTwo)
            } else {
                listOf(forwardByOne)
            }.map { BasicMove(pawn, it) }
                .toSet()
        } else {
            emptySet()
        }

        val captureMoves = setOf(pawn.forwardLeft(), pawn.forwardRight())
            .mapNotNull { board.getSquareOrNull(it) }
            .filter { it isOccupiedBy pawn.theOtherPlayer }
            .map { BasicMove(pawn, it.position, true) }

        return advanceMoves + captureMoves
    }

    /**
     * Returns a set of en passant moves for given [pawn], or an empty set if no en passant
     * moves are possible
     */
    private fun enPassant(pawn: Pawn, board: Board): Set<Move> {
        fun Pawn.backwardBy(n: Int): Position = position - (Direction(rowDirection, 0) * n)
        fun Pawn.advancedTwoSquaresLastTime(): Boolean = history.first() == backwardBy(2)

        return board.getPieces(pawn.theOtherPlayer, Pawn::class)
            .asSequence()
            .filter { it.advancedTwoSquaresLastTime() }
            .filter { enemyPawn ->
                // the enemy pawn must be on the same row, right next to the moving pawn
                val (row, col) = pawn.position
                val (enemyRow, enemyCol) = enemyPawn.position
                row == enemyRow && abs(col - enemyCol) == 1
            }.filter { enemyPawn ->
                // the two-step advance of the enemy pawn must be the last played move
                when (val lastMove = board.playedMoves.last()) {
                    is BasicMove -> lastMove.piece is Pawn && lastMove.to == enemyPawn.position
                    else -> false
                }
            }
            .map { EnPassantMove(pawn, it.backwardBy(1), it) }
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

        if (king.hasMoved || king.isInCheck(board)) return emptySet()

        return board.getPieces(king.player, Rook::class)
            .asSequence()
            .filterNot { it.hasMoved }
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

        return (1 + minOf(from.col, to.col) until maxOf(from.col, to.col))
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
            val newSquare = board.getSquareOrNull(newPos)

            return when {
                newSquare == null -> moves
                newSquare isOccupiedBy piece.player -> moves
                newSquare isOccupiedBy piece.theOtherPlayer -> moves.plus(BasicMove(piece, newPos, true))
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
