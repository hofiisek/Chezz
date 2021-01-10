package board

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import piece.Piece

/**
 * A single square on the board. May or may not be occupied by a piece.
 *
 * @param position position on the board as a pair of x and y coordinates
 * @param piece piece occupying this square, may be null
 *
 * @author Dominik Hoftych
 */
data class Square(val position: Position, var piece: Piece? = null) {

    /**
     * Rank (row) of this square in range from 1 to 8
     */
    val rank: Int = 8 - position.row

    /**
     * File (column) of this square, named from 'a' to 'h'
     */
    val file: Char = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')[position.col]

    /**
     * Text representation of the position (e.g. h8, c5, ..)
     */
    val text: String = "${file}${rank}"

    init {
        require(position.onBoard) {
            "Square out of bounds"
        }
    }

    constructor(row: Int, col: Int, piece: Piece? = null) : this(Position(row,col), piece)
    constructor(other: Square) : this(other.position, other.piece)
    constructor(other: Square, piece: Piece) : this(other.position, piece)

    /**
     * Returns an [ImageView] with image of the piece on this square, if there is any, null otherwise
     */
    fun getPieceImg(): ImageView? = piece?.let {
        ImageView(Image("/pieces/${it.name}.png", 40.0, 40.0, true, true))
    }

    /**
     * Returns true if the square is occupied, i.e. if there's some piece on it
     */
    fun isOccupied(): Boolean = piece == null

    override fun toString(): String {
        return "Square(position='$text', piece='${piece?.name ?: "None"})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Square

        if (position != other.position) return false
        if (rank != other.rank) return false
        if (file != other.file) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + rank
        result = 31 * result + file.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }

}

infix fun Square.occupiedBySameColorAs(otherPiece: Piece): Boolean = this.piece?.player == otherPiece.player
