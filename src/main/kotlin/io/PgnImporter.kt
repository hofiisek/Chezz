package io

import board.Board
import board.Position
import board.playMove
import game.*
import piece.*
import java.io.File
import kotlin.reflect.KClass

object PgnImporter {

    /**
     * Regex matching the tag pairs of the pgn file
     */
    private val tagPairsRgx = "\\[(.*?)]".toRegex()

    /**
     * Regex matching bracket comments in the pgn file ({...comment...})
     */
    private val bracketCommentsRgx = "\\{(.*?)}".toRegex()

    /**
     * Regex matching semicolon comments in the pgn file (;...comment until the end of line...)
     */
    private val semicolonCommentsRgx = ";.*".toRegex()

    /**
     * Regex matching the format of a promotion move,
     * e.g. fxe8=Q (pawn on file "f" to "e8" with capture and promotes to queen"
     */
    private val promotionMoveRgx = """([a-h]x)?[a-h][1-8]=([QRBN])""".toRegex() // ((([a-h])x)?([a-h][1-8]))=([QRBN])

    /**
     * Regex matching the format of a basic move regardless the type of the moving piece,
     * e.g. e6 (pawn to e6), fxe6 (pawn on file "f" to "e6" with capture),
     * Ne6 (knight to e6), Nxe6 (knight to e6 with capture), etc.
     */
    private val castlingMoveRgx = """O-O(-O)?""".toRegex()

    /**
     * Regex matching the format of a castling move,
     * e.g. O-O for kingside castling, and O-O-O for queenside castling
     */
    private val basicMoveRgx = """([KQNBR|a-h])?(x)?([a-h][1-8])""".toRegex()

    /**
     * Extracts the movetext from given [pgnFile] (discarding tag pairs and comments),
     * splits it to strings representing individual moves, and reconstructs the
     * board by applying the moves on it one by one.
     */
    fun importPgn(pgnFile: File): Board {

        // ignore tag pairs and comments
        val moveText: String = pgnFile.inputStream().bufferedReader().readLines()
            .filterNot { it.matches(tagPairsRgx) }
            .map { it.replace(semicolonCommentsRgx, "") }
            .joinToString(" ")
            .replace(bracketCommentsRgx, "")

        val moveList: List<String> = moveText.split("""\d*\.""".toRegex())
            .flatMap { it.split(" ") }
            .filter { it.isNotBlank() }

        println(moveList)

        return reconstruct(Board.INITIAL, moveList.iterator())
    }

    private fun reconstruct(board: Board, rounds: Iterator<String>): Board {
        if (!rounds.hasNext()) return board

        val moveAn: String = rounds.next()
        val move: Move = when {
            moveAn.contains(promotionMoveRgx) -> constructPromotion(board, promotionMoveRgx.find(moveAn)!!)
            moveAn.contains(castlingMoveRgx) -> constructCastling(board, castlingMoveRgx.find(moveAn)!!)
            moveAn.contains(basicMoveRgx) -> constructBasicMove(board, basicMoveRgx.find(moveAn)!!)
            else -> throw IllegalArgumentException("Move $moveAn not recognized")
        }

        println("Move $moveAn  ---> $move")

        return reconstruct(board.playMove(move), rounds)
    }

    private fun constructBasicMove(board: Board, match: MatchResult): BasicMove {
        val (pieceOrFile, captureSign, destPosStr) = match.destructured.toList()
        return when {
            // sanity checks that if piece
//            pieceOrFile.isBlank() && captureSign.isNotBlank() ->
//                throw IllegalArgumentException("Basic move ${match.value} has incorrect format")
//            pieceOrFile.isNotBlank() && captureSign.isBlank() ->
//                throw IllegalArgumentException("Basic move ${match.value} has incorrect format")
            pieceOrFile.isBlank() -> {
                board.getPiecesFor(type = Pawn::class)
                    .flatMap { it.getAllowedMoves(board) }
                    .filterIsInstance<BasicMove>()
                    .filterNot { it.isCapture }
                    .first { it.to == destPosStr.toPosition() }
            }
            pieceOrFile.matches("""[KQNBR]""".toRegex()) -> {
                board.getPiecesFor(type = resolvePieceClass(pieceOrFile))
                    .flatMap { it.getAllowedMoves(board) }
                    .filterIsInstance<BasicMove>()
                    .filter { if (captureSign.isNotBlank()) it.isCapture else true }
                    .first { it.to == destPosStr.toPosition() }
            }
            pieceOrFile.matches("""[a-h]""".toRegex()) -> {
                board.getPiecesFor(type = Pawn::class)
                    .flatMap { it.getAllowedMoves(board) }
                    .filterIsInstance<BasicMove>()
                    .filter { it.isCapture }
                    .filter { it.to == destPosStr.toPosition() }
                    .first { it.piece.position.file == pieceOrFile.first() }
            }
            else -> throw IllegalArgumentException("Basic move ${match.value} has incorrect format")
        }
    }

    private fun constructPromotion(board: Board, match: MatchResult): PromotionMove {
//        val (basicMoveStr, _, _, _, promotedTo) = match.destructured.toList()
        val (basicMoveStr, promotedTo) = match.destructured.toList()
        val basicMove: BasicMove = constructBasicMove(board, basicMoveRgx.find(basicMoveStr)!!)
        val (pawn, destination) = basicMove

        val promotedPiece: Piece = when (promotedTo) {
            "Q" -> Queen(pawn.player, destination)
            "B" -> Bishop(pawn.player, destination)
            "R" -> Rook(pawn.player, destination)
            "N" -> Knight(pawn.player, destination)
            else -> throw IllegalArgumentException("No piece recognized by the letter $promotedTo")
        }

        return PromotionMove(basicMove, promotedPiece)
    }

    private fun constructCastling(board: Board, match: MatchResult): CastlingMove {
        val queenSide = match.groupValues[1].isNotBlank()

        return board.getKing().getAllowedMoves(board)
            .filterIsInstance<CastlingMove>()
            .first { it.queenSide == queenSide }
    }

    private fun resolvePieceClass(letter: String): KClass<out Piece> = when (letter) {
        "Q" -> Queen::class
        "K" -> King::class
        "B" -> Bishop::class
        "R" -> Rook::class
        "N" -> Knight::class
        else -> throw IllegalArgumentException("No piece recognized by the letter $letter")
    }

    private fun String.toPosition(): Position {
        require(length == 2 && this.matches("""[a-h][1-8]""".toRegex()))

        val row = 8 - Character.getNumericValue(this[1])
        val col = "abcdefgh".indexOf(this[0])
        return Position(row, col)
    }

    private operator fun <T> List<T>.component6(): T {
        return get(5)
    }

}

fun main(args: Array<String>) {
//    val board: Board = PgnImporter.importPgn("1. g4 f5 2. gxf5 Nf6 3. e4 Nxe4 4. Bh3 Nxf2 5. Nf3 g5 6. O-O")
//    println(board.playedMoves)

    val a = """[Event "F/S Return Match"]"""
    println(a.matches("""[.*]""".toRegex(RegexOption.DOT_MATCHES_ALL)))
}

