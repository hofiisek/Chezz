package game

import board.Square
import piece.Piece

/**
 * Representing a single move of some piece.
 *
 * @author Dominik Hoftych
 */
data class Move(val movingPiece: Piece, val to: Square)