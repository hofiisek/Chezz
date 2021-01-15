package game

import board.Board
import board.Position
import board.Square
import board.add
import piece.*

/**
 * Moves generator.
 *
 * @author Dominik Hoftych
 */
object MoveGenerator {

    // extension functions on Pair
    private fun Pair<Int, Int>.asPosition() = Position(this.first, this.second)
    private infix fun Pair<Int, Int>.times(n: Int) = Pair(n * this.first, n * this.second)

    // extension functions on Square
    private infix fun Square.isOwnedBy(player: Player): Boolean = this.piece?.player == player

    // extension functions on Piece
    private fun Piece.theOtherPlayer(): Player = if (this.player == Player.WHITE) Player.BLACK else Player.WHITE

    private fun <E> MutableList<E>.addIf(predicate: () -> Boolean, other: Collection<E>): MutableList<E> {
        if (predicate()) this.addAll(other)
        return this
    }

    private fun <E> MutableList<E>.addIf(predicate: () -> Boolean, vararg elements: E): MutableList<E> {
        if (predicate()) this.addAll(elements)
        return this
    }

    fun generate(piece: Piece, board: Board): Set<Move> = when(piece) {
        is Pawn -> pawnMoves(piece, board)
        is Rook -> rookMoves(piece, board)
        is Knight -> knightMoves(piece, board)
        is Bishop -> bishopMoves(piece, board)
        is Queen -> queenMoves(piece, board)
        is King -> kingMoves(piece, board)
    }

    private fun pawnMoves(thisPawn: Piece, board: Board): Set<Move> {
        // direction of movement along the y axis
        val forward = if (thisPawn.player == Player.WHITE) 1 else -1
        val currPos: Position = thisPawn.position

        val movement = mutableListOf(
                // basic move forward by 1 square
                Pair(forward, 0)
        )

        // during first move, pawn can advance two squares if the skipping square is unoccupied
        if (!thisPawn.hasMoved && board.getPiece(currPos add Pair(forward, 0)) == null)
            movement.add(Pair(2 * forward, 0))

        // capture moves
        for (j in arrayOf(-1, 1)) {
            val newPos: Position = currPos add Pair(forward, j)
            val newSquare: Square = board.getSquareOrNull(newPos) ?: continue
            if (newSquare isOwnedBy thisPawn.theOtherPlayer())
                Pair(forward, j)
        }

        return movement
                .map { shift -> currPos add shift }
                .map { pos -> board.getSquare(pos) }
                .filterNotNull()
                .map { square -> Move(thisPawn, square) }
                .toSet()
    }

    private fun rookMoves(thisRook: Piece, board: Board): Set<Move> {
        val movement = setOf(
                Pair(-1, 0), // up
                Pair(0, 1),  // right
                Pair(1, 0),  // down
                Pair(0, -1)  // left
        )

        val moves: MutableList<Move> = mutableListOf()
        val currPos = thisRook.position
        
        for (shift in movement) {
            for (n in 1..7) {
                val newPos = currPos add (shift times n)
                val newSquare = board.getSquareOrNull(newPos) ?: break

                when {
                    newSquare isOwnedBy thisRook.player -> break
                    newSquare isOwnedBy thisRook.theOtherPlayer() -> {
                        moves.add(Move(thisRook, newSquare))
                        break
                    }
                    else -> moves.add(Move(thisRook, newSquare))
                }
            }
        }
        
        return moves.toSet()
    }

    private fun knightMoves(thisKnight: Piece, board: Board): Set<Move> {
        val movement = setOf(
                Pair(-2, 1),  // up->right
                Pair(-1, 2),  // right->up
                Pair(1, 2),   // right->down
                Pair(2, 1),   // down->right
                Pair(2, -1),  // down->left
                Pair(1, -2),  // left->down
                Pair(-1, -2), // left->up
                Pair(-2, -1), // up->left
        )

        val moves: MutableList<Move> = mutableListOf()
        val currPos = thisKnight.position

        for (shift in movement) {
            val newPos = currPos add shift
            val newSquare = board.getSquareOrNull(newPos) ?: break

            when {
                newSquare isOwnedBy thisKnight.player -> continue
                newSquare isOwnedBy thisKnight.theOtherPlayer() -> {
                    moves.add(Move(thisKnight, newSquare))
                    continue
                }
                else -> moves.add(Move(thisKnight, newSquare))
            }
        }

        return moves.toSet()
    }

    private fun bishopMoves(thisBishop: Piece, board: Board): Set<Move> {


//        override val Pairs = (1..7).map {
//            setOf(
//                    Pair(-it,  1), // up-right
//                    Pair(1,  1),  // down-right
//                    Pair(1, - 1),  // down-left
//                    Pair(-it, - 1)  // up-left
//            )
//        }.flatten().toSet()
        return setOf()
    }

    private fun queenMoves(thisQueen: Piece, board: Board): Set<Move> {


//        override val Pairs = (1..7).map {
//            setOf(
//                    // clockwise
//                    Pair(-it, 0),  // up
//                    Pair(-it,  1),  // up-right
//                    Pair(0,  1), // right
//                    Pair(1,  1),  // down-right
//                    Pair(1, 0),  // down
//                    Pair(1, - 1), // down-left
//                    Pair(0, - 1),  // left
//                    Pair(-it, - 1)  // up-left
//            )
//        }.flatten().toSet()
        return setOf()
    }
    private fun kingMoves(thisKing: Piece, board: Board): Set<Move> {


//        override val Pairs = setOf(
//                // clockwise
//                Pair(-1, 0),  // up
//                Pair(-1, 1),  // up-right
//                Pair(0, 1), // right
//                Pair(1, 1),  // down-right
//                Pair(1, 0),  // down
//                Pair(1, -1), // down-left
//                Pair(0, -1),  // left
//                Pair(-1, -1)  // up-left
//        )

        return setOf()
    }


}