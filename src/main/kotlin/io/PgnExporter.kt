package io

import board.Board
import game.Move

typealias Chunk = List<Move>


/**
 * @author Dominik Hoftych
 */
object PgnExporter {

    fun exportToPgn(board: Board): String {
        val result = exportPgnRecursive(board.playedMoves.chunked(2).iterator(), 1, "")
        println(result)

        return result
    }

    private fun exportPgnRecursive(chunks: Iterator<Chunk>, round: Int, content: String): String {
        if (!chunks.hasNext()) return content

        val chunk = chunks.next()
        return exportPgnRecursive(chunks, round + 1, "$content$round. ${chunk.joinToString(separator = " ") { it.an }} ")
    }
}