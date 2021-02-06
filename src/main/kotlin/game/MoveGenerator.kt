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
 * // TODO implement promotion
 *
 * @author Dominik Hoftych
 */
object MoveGenerator {

    /**
     * For given [piece], generates all allowed moves considering the current [board] state
     */
    fun generate(piece: Piece, board: Board): Set<Move> = when(piece) {
        is Pawn -> pawnMoves(piece, board) and enPassant(piece, board)
        is King -> generateMoves(board, piece, 1) and castling(piece, board)
        is Knight -> generateMoves(board, piece, 1)
        is Rook -> generateMoves(board, piece, 7)
        is Bishop -> generateMoves(board, piece, 7)
        is Queen -> generateMoves(board, piece, 7)
    }.filter { it.applyOn(board, simulate = true).validate() }.toSet()

    /**
     * Generates the "basic" moves allowed for the given [pawn]. Such basic moves include classic advance moves
     * by 1 square forward, two square advance moves, and capture moves.
     */
    private fun pawnMoves(pawn: Piece, board: Board): Set<Move> {
        val shiftX = if (pawn.player == Player.WHITE) -1 else +1
        val currPos = pawn.position

        val movement: MutableList<Direction> = mutableListOf()

        // basic move forward by 1 square
        with(Direction(shiftX, 0)) {
            if (board.getSquare(currPos add this).isUnoccupied) {
                movement.add(this)
            }
        }

        // move forward by 2 squares, if it is the first move and the skipping square is free
        if (!pawn.hasMoved && board.getSquare(currPos add Direction(shiftX, 0)).isUnoccupied)
            movement.add(Direction(2 * shiftX, 0))

        // capture moves
        for (shiftY in arrayOf(-1, 1)) {
            val newPos = currPos add Direction(shiftX, shiftY)
            val newSquare = board.getSquareOrNull(newPos) ?: continue

            if (newSquare occupiedBy pawn.theOtherPlayer) {
                movement.add(Direction(shiftX, shiftY))
            }
        }

        return movement
                .map { shift -> currPos add shift }
                .map { pos -> board.getSquare(pos) }
                .map { square -> BasicMove(pawn, square)}
                .toSet()
    }

    /**
     * Returns a set of en passant moves for given [pawn], or an empty set if no en passant
     * moves are available considering the given [board] state.
     */
    private fun enPassant(pawn: Piece, board: Board): Set<Move> {
        val enemyPawns: List<Pawn> = board.getPiecesFor(pawn.theOtherPlayer, Pawn::class)

        val moves: MutableSet<Move> = mutableSetOf()

        for (enemyPawn in enemyPawns) {
            val skippedSquare: Position = enemyPawn.advancedTwoSquares() ?: continue

            val (row, col) = pawn.position
            val (enemyRow, enemyCol) = enemyPawn.position
            if (row != enemyRow) continue
            if (abs(col - enemyCol) != 1) continue

            // TODO check that this is done the very next move after enemy pawn's two-step advance
            moves.add(EnPassantMove(pawn, board.getSquare(skippedSquare), enemyPawn))
        }

        return moves
    }

    /**
     * Helper property when evaluating the possibility of en passant moves.
     *
     * If the pawn advanced two squares as his last move, the returned position
     * is the position of the square that was skipped during the two-square move,
     * otherwise it's null.
     */
    private fun Pawn.advancedTwoSquares(): Position? = when {
        history.size != 1 -> null
        player == Player.BLACK && position.row != 3 -> null
        player == Player.WHITE && position.row != 4 -> null
        else -> {
            val shiftX = if (player == Player.WHITE) 1 else -1
            (position add Position(shiftX, 0))
        }
    }

    /**
     * Returns a set of castling moves for given [king], or an empty set if castling is not possible
     * considering the given [board] state.
     */
    private fun castling(king: Piece, board: Board): Set<Move> {
        if (king.hasMoved) return emptySet()

        val rooks: List<Rook> = board.getPiecesFor(king.player, Rook::class)
                .filterNot { it.hasMoved }
                .ifEmpty { return emptySet() }

        return rooks
                .filter { rook -> squaresBetween(rook, king, board).none { it.piece != null } }
                .map { rook ->
                    val queenSide: Boolean = (rook.position diffCols king.position) == 4
                    if (queenSide) {
                        CastlingMove(rook to board.getSquare(rook moveRightBy 3), king to board.getSquare(king moveLeftBy 2), queenSide)
                    } else {
                        CastlingMove(rook to board.getSquare(rook moveLeftBy  2), king to board.getSquare(king moveRightBy  2), queenSide)
                    }
                }.toSet()
    }

    /**
     * Returns a list of squares that are between the given [from] and [to] pieces
     */
    private fun squaresBetween(from: Piece, to: Piece, board: Board): List<Square> {
        require(from.position.row == to.position.row)

        val fromCol = from.position.col
        val toCol = to.position.col
        return (1 + minOf(fromCol, toCol) until maxOf(fromCol, toCol))
                .map { Position(to.position.row, it) }
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
                newSquare occupiedBy piece.theOtherPlayer -> moves and BasicMove(piece, newSquare)
                else -> generateMovesRecursive(dir, n + 1, moves and BasicMove(piece, newSquare))
            }
        }

        return piece.movement.flatMap { generateMovesRecursive(it, 1) }.toSet()
    }

    /**
     * Multiplies the receiver [Direction] by [n] along both axes
     */
    private infix fun Direction.times(n: Int) = Direction(n * this.first, n * this.second)

    /**
     * Infixed and renamed [Set.plus] method
     */
    private infix fun Set<Move>.and(other: Set<Move>): Set<Move> = this.plus(other)

    /**
     * Infixed and renamed [Set.plus] method
     */
    private infix fun Set<Move>.and(other: Move): Set<Move> = this.plus(other)

}

/**
 * Direction along x and y axis respectively
 */
typealias Direction = Pair<Int, Int>