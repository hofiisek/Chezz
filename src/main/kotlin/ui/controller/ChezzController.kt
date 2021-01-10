package ui.controller

import board.Board
import board.Square
import board.occupiedBySameColorAs
import game.Move
import piece.Piece
import tornadofx.Controller
import ui.view.ChezzView

class ChezzController : Controller() {

    private val view: ChezzView by inject()
    private val board: Board = Board()

    private var selectedPiece: Piece? = null


    fun onSquareClicked(clickedSquare: Square) {
        println("Clicked on square: $clickedSquare")

        if (selectedPiece == null) {
            selectPiece(clickedSquare.piece)
        } else {
            moveOrReselect(clickedSquare)
        }
    }

    fun resetSelection() {
        selectedPiece = null
        view.repaintBoard()
    }

    private fun selectPiece(clickedPiece: Piece?) {
        selectedPiece = clickedPiece

        val allowedMoves: Set<Move> = selectedPiece?.getAllowedMoves(board) ?: emptySet()
        view.renderAllowedMoves(allowedMoves)
        view.renderSelectedPiece(selectedPiece!!)
    }

    private fun moveOrReselect(clickedSquare: Square) {
        if (clickedSquare occupiedBySameColorAs selectedPiece!!) {
            resetSelection()
            selectPiece(clickedSquare.piece)
        }


        val move = Move(selectedPiece!!, clickedSquare)

    }

    fun getSquares(): List<List<Square>> = board.squares

}