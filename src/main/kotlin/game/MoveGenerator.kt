package game

import board.*
import piece.*
import kotlin.math.abs

/**
 * General moves generator.
 *
 * Basic moves are generated recursively for each movement direction for all pieces except for pawn,
 * whose movement, despite of being the most limited one, is quite special.
 *
 * Castling moves are generated for the king, because king's final destination after castling (file 'g' or 'c')
 * is non-reachable from his original position otherwise (in case of the rook, it would interfere with its other moves)
 *
 * @author Dominik Hoftych
 */
object MoveGenerator {

    /**
     * For given [piece], generates all allowed moves considering the current [board] state.
     * If [validateForCheck] is true, for each move the it is validated that it does not put or leave
     * its own king in check.
     */
    fun generate(piece: Piece, board: Board, validateForCheck: Boolean = true): Set<Move> = when(piece) {
        is Pawn -> pawnMoves(piece, board).plus(enPassant(piece, board))
        is King -> generateMoves(board, piece, 1).plus(castling(piece, board, validateForCheck))
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
        val moves: MutableSet<Move> = mutableSetOf()

        // basic move forward by 1 square
        val forwardPos = pawn.forward()
        if (board.getSquareOrNull(forwardPos)?.isUnoccupied == true) {
            moves.add(BasicMove(pawn, forwardPos))

            // move forward by 2 squares, if it is the first move and the skipping square is free
            val forwardPosByTwo = pawn.forward(2)
            if (!pawn.hasMoved && board.getSquareOrNull(forwardPosByTwo)?.isUnoccupied == true) {
                moves.add(BasicMove(pawn, forwardPosByTwo))
            }
        }

        // capture moves
        val forwardLeft = pawn.forwardLeft()
        if (board.getSquareOrNull(forwardLeft)?.occupiedBy(pawn.player.theOtherPlayer) == true) {
            moves.add(BasicMove(pawn, forwardLeft, true))
        }
        val forwardRight = pawn.forwardRight()
        if (board.getSquareOrNull(forwardRight)?.occupiedBy(pawn.player.theOtherPlayer) == true) {
            moves.add(BasicMove(pawn, forwardRight, true))
        }

        return moves
    }

    /**
     * Returns a set of en passant moves for given [pawn], or an empty set if no en passant
     * moves are available considering the given [board] state.
     */
    private fun enPassant(pawn: Pawn, board: Board): Set<Move> {
        val enemyPawns: List<Pawn> = board.getPiecesFor(pawn.theOtherPlayer, Pawn::class)
        return enemyPawns
            .filter { enemyPawn ->
                if (!enemyPawn.advancedTwoSquares()) return@filter false

                val (row, col) = pawn.position
                val (enemyRow, enemyCol) = enemyPawn.position
                if (row != enemyRow || abs(col - enemyCol) != 1) return@filter false

                val enemyPawnPrevPosition = enemyPawn.backward(2)
                board.previousBoard?.getSquare(enemyPawnPrevPosition)?.piece is Pawn
            }
            .map { enemyPawn -> EnPassantMove(pawn, enemyPawn.backward(), enemyPawn) }
            .toSet()
    }

    /**
     * Returns true if the pawn advanced two squares right in his previous move
     */
    private fun Pawn.advancedTwoSquares(): Boolean = when {
        history.size != 1 -> false
        player == Player.BLACK && position.row != 3 -> false
        player == Player.WHITE && position.row != 4 -> false
        else -> true
    }

    /**
     * Returns the position of the pawn if it moves forward by [n]
     */
    private fun Pawn.forward(n: Int = 1): Position = this.position add (Direction(rowDirection, 0) times n)

    /**
     * Returns the position of the pawn if it moves backwards by [n]
     */
    private fun Pawn.backward(n: Int = 1): Position = this.position sub (Direction(rowDirection, 0) times n)

    /**
     * Returns the position of the pawn if it moves forward left
     */
    private fun Pawn.forwardLeft(): Position = this.position add Direction(rowDirection, -1)

    /**
     * Returns the position of the pawn if it moves forward right
     */
    private fun Pawn.forwardRight(): Position = this.position add Direction(rowDirection, +1)

    /**
     * Returns a set of castling moves for given [king], or an empty set if castling is not possible
     * considering the given [board] state.
     */
    private fun castling(king: Piece, board: Board, validateForCheck: Boolean): Set<Move> {
        // avoid infinite recursion when determining whether some position or the king is in check
        if (!validateForCheck) return emptySet()

        val rooks: List<Rook> = board.getPiecesFor(king.player, Rook::class)
                .filterNot { it.hasMoved }

        if (rooks.isEmpty() || king.hasMoved || king.isInCheck(board)) return emptySet()

        return rooks
            .filter { rook ->
                // there must be no pieces between the castling pieces
                squaresBetween(rook.position, king.position, board).none { it.piece != null }
            }
            .filter { rook ->
                // squares crossed by king must not be in check
                val queenSide: Boolean = abs(rook.position.col - king.position.col) == 4
                val kingDestination: Position = if (queenSide) king moveLeftBy 2 else king moveRightBy 2
                squaresBetween(king.position, kingDestination, board).none {
                    it.isInCheck(board)
                }
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
            .map { Position(to.row, it) }
            .map { board.getSquare(it) }
    }

    /**
     * Returns the position which [n] squares to the right of the receiver piece
     */
    private infix fun Piece.moveRightBy(n: Int) = Position(position.row, position.col + n)

    /**
     * Returns the position which [n] squares to the right of the receiver piece
     */
    private infix fun Piece.moveLeftBy(n: Int) = Position(position.row, position.col - n)


    /**
     * Recursively generates moves for given [piece] until either:
     * - an own piece is hit, or
     * - an enemy piece is hit, in such case the move is still included, or
     * - the [maxDistance] parameter is met.
     */
    private fun generateMoves(board: Board, piece: Piece, maxDistance: Int): Set<Move> {
        fun generateMovesRecursive(dir: Direction, n: Int, moves: Set<Move> = emptySet()): Set<Move> {
            if (n > maxDistance) return moves

            val newPos = piece.position add (dir times n)
            val newSquare = board.getSquareOrNull(newPos) ?: return moves

            return when {
                newSquare occupiedBy piece.player -> moves
                newSquare occupiedBy piece.theOtherPlayer -> moves.plus(BasicMove(piece, newPos, true))
                else -> generateMovesRecursive(dir, n + 1, moves.plus(BasicMove(piece, newPos)))
            }
        }

        return piece.movement.flatMap { generateMovesRecursive(it, 1) }.toSet()
    }

    /**
     * Multiplies the receiver [Direction] by [n] along both axes
     */
    private infix fun Direction.times(n: Int) = Direction(n * this.first, n * this.second)

}

/**
 * Direction along x and y axis respectively
 */
typealias Direction = Pair<Int, Int>