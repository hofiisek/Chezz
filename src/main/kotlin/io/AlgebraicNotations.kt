package io

import board.Board
import board.Position
import game.*
import io.AmbiguityLevel.*
import piece.*

/**
 * Unicode character representing the piece.
 * Currently unused - will be used once importing/exporting of PGN
 * files with unicode symbols of pieces is implemented.
 */
val Piece.unicode: String
    get() = when (this) {
        is Pawn -> if (player == Player.WHITE) "\u2659" else "\u265F"
        is Rook -> if (player == Player.WHITE) "\u2656" else "\u265C"
        is Knight -> if (player == Player.WHITE) "\u2658" else "\u265E"
        is Bishop -> if (player == Player.WHITE) "\u2657" else "\u265D"
        is Queen -> if (player == Player.WHITE) "\u2655" else "\u265B"
        is King -> if (player == Player.WHITE) "\u2654" else "\u265A"
    }

val Position.an: String get() = "${file}$rank"

/**
 * Letter representing the piece
 */
val Piece?.letter: String
    get() = when (this) {
        is Bishop -> "B"
        is King -> "K"
        is Knight -> "N"
        is Pawn -> ""
        is Queen -> "Q"
        is Rook -> "R"
        null -> throw IllegalArgumentException()
    }

/**
 * Algebraic notation of the move
 */
val Move.an: String
    get() = when (this) {
        is BasicMove ->
            piece.letter +
                (if (isCapture && piece is Pawn) piece.position.file else "").toString() +
                (if (isCapture) "x" else "") +
                to.an
        is PromotionMove -> "${basicMove.an}=${promotedTo.letter}"
        is CastlingMove -> "O-O${if (queenSide) "-0" else ""}"
        is EnPassantMove -> "${pawn.position.file}x${to.an}"
    }

/**
 * Algebraic notation of the move with the file of the origin position included
 */
val BasicMove.anWithFile: String
    get() = when (piece) {
        is Pawn -> this.an
        else -> "${piece.letter}${piece.position.file}${this.an.drop(1)}"
    }

/**
 * Algebraic notation of the move with the rank of the origin position included
 */
val BasicMove.anWithRank: String
    get() = when (piece) {
        is Pawn -> this.an
        else -> "${piece.letter}${piece.position.rank}${this.an.drop(1)}"
    }

/**
 * Algebraic notation of the move with the origin position included
 */
val BasicMove.anWithPosition: String
    get() = when (piece) {
        is Pawn -> this.an
        else -> "${piece.letter}${piece.position.an}${this.an.drop(1)}"
    }

/**
 * Returns the correct algebraic notation of this move considering
 * the current [board], resolving all possible unambiguity if there
 * are multiple pieces of the same type that can move to the same destination.
 *
 * Note that ambiguity can happen only for basic moves where the moving piece
 * is not a pawn.
 */
fun Move.getAlgebraicNotation(board: Board): String = when {
    this is BasicMove && this.piece !is Pawn -> {
        // different pieces that can move to the same square
        val otherPieces: List<Piece> = board.getPieces(type = this.piece::class)
            .filter { it != this.piece }
            .flatMap { it.getAllowedMoves(board).filterIsInstance<BasicMove>() }
            .filter { it.to == this.to }
            .map { it.piece }

        when (resolveAmbiguityLevel(this, otherPieces)) {
            RANK_AND_FILE -> this.anWithPosition
            RANK -> this.anWithFile
            FILE -> this.anWithRank
            DEFAULT -> this.anWithFile
            else -> this.an
        }
    }
    else -> this.an
}

/**
 * Resolves the ambiguity level of the given [move] and the list of [pieces] that
 * can move to the same destination
 */
private fun resolveAmbiguityLevel(move: BasicMove, pieces: List<Piece>): AmbiguityLevel {
    fun Piece.sameRow(row: Int) = this.position.row == row
    fun Piece.sameColumn(column: Int) = this.position.col == column

    val (row, col) = move.piece.position
    return when {
        pieces.isEmpty() -> NONE
        pieces.any { it.sameRow(row) } && pieces.any { it.sameColumn(col) } -> RANK_AND_FILE
        pieces.any { it.sameRow(row) } -> RANK
        pieces.any { it.sameColumn(col) } -> FILE
        else -> DEFAULT
    }
}

/**
 * Ambiguity levels that need to be resolved to ensure that the
 * algebraic notation of a move is unambiguous w.r.t. current board state.
 */
private enum class AmbiguityLevel {

    /**
     * There are multiple pieces of the same type on the same rank as well as
     * on the same file, so both the rank and the file must be
     * used to ensure unambiguity.
     */
    RANK_AND_FILE,

    /**
     * There are multiple pieces of the same type on the same rank,
     * so the file must be used to ensure unambiguity
     */
    RANK,

    /**
     * There are multiple pieces of the same type on the same file,
     * so the rank must be used to ensure unambiguity
     */
    FILE,

    /**
     * There are no other pieces on the same file nor on the same rank.
     * In such situation, it's a convention to use the file to ensure
     * unambiguity.
     */
    DEFAULT,

    /**
     * There are no pieces that can move to the same destination,
     * standard algebraic notation can be used
     */
    NONE
}
