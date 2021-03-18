package board

import game.BasicMove
import game.Player
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import piece.Piece
import piece.moveTo
import randomEmptyPositionOtherThan
import randomPositionOtherThan

/**
 * @author Dominik Hoftych
 */
class BoardTest : StringSpec({

    "an empty board isn't occupied by any pieces" {
        Board.emptyBoard().let {
            it.getPieces(Player.BLACK) shouldBe emptyList()
            it.getPieces(Player.WHITE) shouldBe emptyList()
        }
    }

    "playing a basic move updates the board correctly" {
        repeat(20) { _ ->
            val oldBoard = Board.initialBoard()
            val piece = oldBoard.getPieces().random()

            // such a move might be invalid but that doesn't matter for this test
            val destination = randomEmptyPositionOtherThan(oldBoard, piece.position)
            val move = BasicMove(piece, destination)

            val expectedPiece = piece moveTo destination
            val expectedUnaffectedSquares = oldBoard.squares
                .filter { it.position != piece.position }
                .filter { it.position != destination }
                .map { it.position }

            oldBoard.playMove(move).let { newBoard ->
                newBoard.previousBoard shouldBe oldBoard
                newBoard.playerOnTurn shouldNotBe oldBoard.playerOnTurn
                newBoard.playedMoves shouldContainExactly listOf(move)
                newBoard.getSquare(piece.position).piece shouldBe null
                newBoard.getSquare(destination).piece shouldBe expectedPiece

                // the rest of the squares remains unchanged
                expectedUnaffectedSquares.forEach { position ->
                    newBoard.getSquare(position) shouldBe oldBoard.getSquare(position)
                }
            }
        }
    }

    "getting pieces of some type returns pieces only of that type" {
        Board.initialBoard().let { board ->
            Piece::class.sealedSubclasses.forEach { cls ->
                board.getPieces(type = cls).forEach { it::class shouldBe cls }
            }
        }
    }

})