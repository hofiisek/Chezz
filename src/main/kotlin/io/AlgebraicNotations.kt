package io

import board.Position
import board.Square
import game.*
import piece.*


/**
 * The unicode character of the piece
 */
val Piece.unicode: String
    get() = when(this) {
        is Pawn -> if (player == Player.WHITE) "\u2659" else "\u265F"
        is Rook -> if (player == Player.WHITE) "\u2656" else "\u265C"
        is Knight -> if (player == Player.WHITE) "\u2658" else "\u265E"
        is Bishop -> if (player == Player.WHITE) "\u2657" else "\u265D"
        is Queen -> if (player == Player.WHITE) "\u2655" else "\u265B"
        is King -> if (player == Player.WHITE) "\u2654" else "\u265A"
    }

val Position.an: String
    get() = "${file}${rank}"

val Square.an: String
    get() = position.an

val Piece?.an: String
    get() = when (this) {
        is Bishop -> "B"
        is King -> "K"
        is Knight -> "N"
        is Pawn -> ""
        is Queen -> "Q"
        is Rook -> "R"
        null -> throw IllegalArgumentException()
    }

val Move.an: String
    get() = when (this) {
        // TODO add + sigh if the move is check move
        is BasicMove -> "${piece.an}${if (isCapture && piece is Pawn) piece.position.file else ""}${if (isCapture) "x" else ""}${to.an}"
        is PromotionMove -> "${basicMove.an}=${promotedTo.an}"
        is CastlingMove -> "O-O${if (queenSide) "-0" else ""}"
        is EnPassantMove -> "${pawn.position.file}x${to.an}"
    }