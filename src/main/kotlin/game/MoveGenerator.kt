package game

import board.*
import game.MoveGenerator.moveLeftBy
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
 * // TODO implement promotion
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
        is King -> generateMoves(board, piece, 1).plus(castling(piece, board))
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
        if (board.getSquare(forwardPos).isUnoccupied) {
            moves.add(BasicMove(pawn, forwardPos))

            // move forward by 2 squares, if it is the first move and the skipping square is free
            val forwardPosByTwo = pawn.forward(2)
            if (!pawn.hasMoved && board.getSquare(forwardPosByTwo).isUnoccupied) {
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
     * Returns the position of the pawn if it moves forward by [n]
     */
    private fun Pawn.forward(n: Int = 1): Position = this.position add (Direction(rowDirection, 0) times n)

    /**
     * Returns the position of the pawn if it moves forward left
     */
    private fun Pawn.forwardLeft(): Position = this.position add Direction(rowDirection, -1)

    /**
     * Returns the position of the pawn if it moves forward right
     */
    private fun Pawn.forwardRight(): Position = this.position add Direction(rowDirection, +1)

    /**
     * Returns a set of en passant moves for given [pawn], or an empty set if no en passant
     * moves are available considering the given [board] state.
     */
    private fun enPassant(pawn: Piece, board: Board): Set<Move> {
        val enemyPawns: List<Pawn> = board.getPiecesFor(pawn.theOtherPlayer, Pawn::class)

        val moves: MutableSet<Move> = mutableSetOf()

        for (enemyPawn in enemyPawns) {
            val skippedPosition: Position = enemyPawn.advancedTwoSquares() ?: continue

            val (row, col) = pawn.position
            val (enemyRow, enemyCol) = enemyPawn.position
            if (row != enemyRow || abs(col - enemyCol) != 1) continue

            val lastPlayedMove = board.playedMoves.last().takeIf { it is BasicMove } ?: continue
//            if ((lastPlayedMove as BasicMove).piece is Pawn)
            
            // TODO check that this is done the very next move after enemy pawn's two-step advance
            moves.add(EnPassantMove(pawn, skippedPosition, enemyPawn))
        }

        return moves
    }

    /**
     * Helper function evaluating the possibility of en passant moves.
     *
     * If the pawn advanced two squares as his last move, the returned position
     * is the position of the square that was skipped during the two-square move,
     * otherwise it's null.
     */
    private fun Pawn.advancedTwoSquares(): Position? = when {
        history.size != 1 -> null
        player == Player.BLACK && position.row != 3 -> null
        player == Player.WHITE && position.row != 4 -> null
        else -> position sub Direction(rowDirection, 0)
    }

    /**
     * Returns a set of castling moves for given [king], or an empty set if castling is not possible
     * considering the given [board] state.
     */
    private fun castling(king: Piece, board: Board): Set<Move> {
        val rooks: List<Rook> = board.getPiecesFor(king.player, Rook::class)
                .filterNot { it.hasMoved }

        if (rooks.isEmpty() || king.hasMoved) return emptySet()

        // TODO check that squares crossed by king are not under attack
        return rooks
            .filter { rook -> squaresBetween(rook.position, king.position, board).none { it.piece != null } }
//            .filter { rook ->
//                val queenSide: Boolean = abs(rook.position.col - king.position.col) == 4
//                val kingDestination: Position = if (queenSide) king moveLeftBy 2 else king moveRightBy 2
//                squaresBetween(king.position, kingDestination, board).none {
//                    it.isInCheck(board)
//                }
//            }
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