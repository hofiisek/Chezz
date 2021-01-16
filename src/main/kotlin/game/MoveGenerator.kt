package game

import board.Board
import board.Square
import board.add
import piece.*

private typealias Direction = Pair<Int, Int>

/**
 * General moves generator.
 *
 * Basic moves are generated recursively for each movement direction for all pieces apart from pawn,
 * whose movement, despite of being the most limited one, is quite special.
 *
 * Castling moves are generated for the king, because king's final destination after castling (file 'g' or 'c')
 * is non-reachable from his original position otherwise (in case of the rook, it could interfere with its other moves)
 *
 * @author Dominik Hoftych
 */
object MoveGenerator {

    /**
     * For given [piece], generates all allowed moves considering the current [board] state.
     */
    fun generate(piece: Piece, board: Board): Set<Move> = when(piece) {
        is Pawn -> pawnMoves(piece, board)
        is Rook -> rookMoves(piece, board)
        is Knight -> knightMoves(piece, board)
        is Bishop -> bishopMoves(piece, board)
        is Queen -> queenMoves(piece, board)
        is King -> kingMoves(piece, board)
    }

    private fun pawnMoves(thisPawn: Piece, board: Board): Set<Move> {
        val shiftX = if (thisPawn.player == Player.WHITE) -1 else +1
        val currPos = thisPawn.position

        val movement: MutableList<Direction> = mutableListOf()

        // basic move forward by 1 square
        Direction(shiftX, 0)
                .takeUnless { board.getSquare(currPos add it).isOccupied }
                ?.let { movement.add(it) }

        // move forward by 2 squares, if it is the first move and the skipping square is free
        if (!thisPawn.hasMoved && !board.getSquare(currPos add Direction(shiftX, 0)).isOccupied)
            movement.add(Direction(2 * shiftX, 0))

        // capture moves
        for (shiftY in arrayOf(-1, 1)) {
            val newPos = currPos add Direction(shiftX, shiftY)
            val newSquare = board.getSquareOrNull(newPos) ?: continue

            if (newSquare belongsTo thisPawn.theOtherPlayer()) {
                movement.add(Direction(shiftX, shiftY))
            }
        }

        // en passant
        // TODO


        return movement
                .map { shift -> currPos add shift }
                .map { pos -> board.getSquare(pos) }
                .map { square -> Move(thisPawn, square) }
                .toSet()
    }

    private fun rookMoves(thisRook: Piece, board: Board): Set<Move> {
        val movement = setOf(
                Direction(-1, 0), // up
                Direction(0, 1),  // right
                Direction(1, 0),  // down
                Direction(0, -1)  // left
        )

        return movement.flatMap { generateMoves(board, thisRook, it, 7) }.toSet()
    }

    private fun knightMoves(thisKnight: Piece, board: Board): Set<Move> {
        val movement = setOf(
                Direction(-2, 1),  // up->right
                Direction(-1, 2),  // right->up
                Direction(1, 2),   // right->down
                Direction(2, 1),   // down->right
                Direction(2, -1),  // down->left
                Direction(1, -2),  // left->down
                Direction(-1, -2), // left->up
                Direction(-2, -1), // up->left
        )

        return movement.flatMap { generateMoves(board, thisKnight, it, 1) }.toSet()
    }

    private fun bishopMoves(thisBishop: Piece, board: Board): Set<Move> {
        val movement = setOf(
                Direction(-1,  1), // up-right
                Direction(1,  1),  // down-right
                Direction(1, - 1),  // down-left
                Direction(-1, - 1)  // up-left
        )

        return movement.flatMap { generateMoves(board, thisBishop, it, 7) }.toSet()
    }

    private fun queenMoves(thisQueen: Piece, board: Board): Set<Move> {
        val movement = setOf(
                Direction(-1, 0),  // up
                Direction(-1,  1),  // up-right
                Direction(0,  1), // right
                Direction(1,  1),  // down-right
                Direction(1, 0),  // down
                Direction(1, - 1), // down-left
                Direction(0, - 1),  // left
                Direction(-1, - 1)  // up-left
        )

        return movement.flatMap { generateMoves(board, thisQueen, it, 7) }.toSet()
    }

    private fun kingMoves(thisKing: Piece, board: Board): Set<Move> {
        val movement = setOf(
                Direction(-1, 0),  // up
                Direction(-1,  1),  // up-right
                Direction(0,  1), // right
                Direction(1,  1),  // down-right
                Direction(1, 0),  // down
                Direction(1, - 1), // down-left
                Direction(0, - 1),  // left
                Direction(-1, - 1)  // up-left
        )

        return movement.flatMap { generateMoves(board, thisKing, it, 1) }.toSet()
    }

    /**
     * Recursively generates moves for given [Piece] along given [Direction],
     * until either
     *      an own piece is hit, or
     *      an opposite piece is hit, in such case the move is still included, or
     *      the [maxDistance] is met.
     */
    private fun generateMoves(board: Board, piece: Piece, direction: Direction, maxDistance: Int): Set<Move> {
        fun generateMovesRecursive(dir: Direction, n: Int, moves: Set<Move> = emptySet()): Set<Move> {
            if (n > maxDistance) return moves

            val newPos = piece.position add (dir times n)
            val newSquare = board.getSquareOrNull(newPos) ?: return moves

            return when {
                newSquare belongsTo piece.player -> moves
                newSquare belongsTo piece.theOtherPlayer() -> {
                    moves.plus(Move(piece, newSquare))
                }
                else -> generateMovesRecursive(dir, n+1, moves.plus(Move(piece, newSquare)))
            }
        }

        return generateMovesRecursive(direction, 1)
    }

    /**
     * Multiplies the receiver [Direction] by n along both axes
     */
    private infix fun Direction.times(n: Int) = Direction(n * this.first, n * this.second)

    /**
     * Returns true if the receiver [Square] belongs to given [Player]
     */
    private infix fun Square.belongsTo(player: Player): Boolean = this.piece?.player == player

    /**
     * Returns the other [Player] of the receiver [Piece]
     */
    private fun Piece.theOtherPlayer(): Player = if (this.player == Player.WHITE) Player.BLACK else Player.WHITE



}