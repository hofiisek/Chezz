package io

import board.Board
import board.playMove
import game.*
import piece.*
import java.io.File
import kotlin.reflect.KClass

/**
 * Importer of files in the standard Portable Game Notation (PGN) format.
 *
 * @author Dominik Hoftych
 */
object PgnImporter {

    /**
     * Regex matching the tag pairs
     */
    private val tagPairsRgx = """\[(.*?)]""".toRegex()

    /**
     * Regex matching bracket comments ({...comment...})
     */
    private val bracketCommentsRgx = """\{(.*?)}""".toRegex()

    /**
     * Regex matching semicolon comments (;...comment until the end of line...)
     */
    private val semicolonCommentsRgx = ";.*".toRegex()

    /**
     * Regex matching the # and + characters
     */
    private val checkSignsRgx = """[#+]""".toRegex()

    /**
     * Regex matching the game result
     */
    private val gameResultRgx = """1/2-1/2|1-0|0-1|\*""".toRegex()

    /**
     * Regex matching the format of moves of any piece type except for pawn.
     * e.g. Ne6 (knight to e6), Nxe6 (knight to e6 with capture)
     */
    private val basicMoveRgx = """([KQNBR])[a-h]?[1-8]?x?[a-h][1-8][#|+]?""".toRegex()

    /**
     * Regex matching the format of moves of pawn.
     * e.g. e6 (pawn to e6), fxe6 (pawn on file f to e6 with capture)
     */
    private val pawnMoveRgx = """([a-h]x)?[a-h][1-8][#|+]?""".toRegex()

    /**
     * Regex matching the format of promotion moves.
     * e.g. fxg8=Q (pawn on file f to g8 with capture and promotes to queen)
     */
    private val promotionMoveRgx = """(([a-h]x)?[a-h][1-8])=([QRBN])[#|+]?""".toRegex()

    /**
     * Regex matching the format of castling moves.
     * O-O for kingside castling, and O-O-O for queenside castling
     */
    private val castlingMoveRgx = """O-O(-O)?[#|+]?""".toRegex()

    /**
     * Extracts the movetext from given [pgnFile] discarding tag pairs, comments, and the
     * game result, splits it to the algebraic notations representing individual moves,
     * and reconstructs the board by applying the moves on it one by one.
     */
    fun importPgn(pgnFile: File): Board {
        val movetext: String = pgnFile.inputStream().bufferedReader().use { reader ->
            reader.readLines()
                .filterNot { line -> line.matches(tagPairsRgx) }
                .joinToString(" ") { line ->
                    // semicolon comment continues to the end of the line,
                    // we need to remove it before joining
                    line.replace(semicolonCommentsRgx, "")
                }
                .replace(bracketCommentsRgx, "")
                .replace(checkSignsRgx, "")
                .replace(gameResultRgx, "")
        }

        val moves: List<String> = movetext.split("""\d*\.""".toRegex())
            .flatMap { it.split(" ") }
            .filter { it.isNotBlank() }

        return reconstruct(Board.initialBoard(), moves.iterator())
    }

    /**
     * Recursively reconstructs the board by performing the given [moves] one by one
     * on the given [board], starting with an initial board.
     */
    private fun reconstruct(board: Board, moves: Iterator<String>): Board {
        if (!moves.hasNext()) return board

        val moveAn: String = moves.next()
        val move: Move = when {
            moveAn.matches(castlingMoveRgx) -> resolveCastlingMove(board, moveAn)
            moveAn.matches(pawnMoveRgx) -> resolvePawnMove(board, moveAn)
            moveAn.matches(basicMoveRgx) -> resolveBasicMove(board, basicMoveRgx.find(moveAn)!!)
            moveAn.matches(promotionMoveRgx) -> resolvePromotionMove(board, promotionMoveRgx.find(moveAn)!!)
            else -> throw IllegalArgumentException("Move $moveAn not recognized")
        }

        return reconstruct(board.playMove(move), moves)
    }

    /**
     * Returns the only castling move matching given [castlingPgn] string.
     */
    private fun resolveCastlingMove(board: Board, castlingPgn: String): CastlingMove {
        return board.getKing().getAllowedMoves(board)
            .filterIsInstance<CastlingMove>()
            .firstOrNull { it.an == castlingPgn }
            ?: throw IllegalStateException("Castling move $castlingPgn is not possible")
    }

    /**
     * Returns the only basic move matching the pgn string stored in given [match] result.
     */
    private fun resolveBasicMove(board: Board, match: MatchResult): BasicMove {
        val (basicMoveStr, pieceLetter) = match.groupValues

        return board.getPiecesFor(type = pieceLetter.pieceClass())
            .flatMap { it.getAllowedMoves(board) }
            .filterIsInstance<BasicMove>()
            .firstOrNull { it.getAlgebraicNotation(board) == basicMoveStr }
            ?: throw IllegalStateException("Move $basicMoveStr not possible on current board")
    }

    /**
     * Returns the only pawn move matching given [pawnMovePgn].
     */
    private fun resolvePawnMove(board: Board, pawnMovePgn: String): Move {
        return board.getPiecesFor(type = Pawn::class)
            .flatMap { it.getAllowedMoves(board) }
            .firstOrNull { it.an == pawnMovePgn }
            ?: throw IllegalStateException("Pawn move $pawnMovePgn is not possible")
    }

    /**
     * Returns the only promotion move matching the pgn string stored in given [match] result.
     */
    private fun resolvePromotionMove(board: Board, match: MatchResult): PromotionMove {
        val (basicMoveStr, _, promotedTo) = match.destructured.toList()
        val basicMove: BasicMove = resolvePawnMove(board, basicMoveStr) as BasicMove
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

    /**
     * Returns the piece class represented by this letter.
     */
    private fun String.pieceClass(): KClass<out Piece> = when (this) {
        "Q" -> Queen::class
        "K" -> King::class
        "B" -> Bishop::class
        "R" -> Rook::class
        "N" -> Knight::class
        else -> throw IllegalArgumentException("No piece recognized by the letter $this")
    }

}