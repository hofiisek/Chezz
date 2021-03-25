package board

/**
 * A matrix of [n] rows and [m] cols with each element initialized using the given [initializer].
 *
 * @param n number of rows
 * @param m number of cols
 * @param initializer lambda function providing row and column indices to initialize
 * individual matrix elements
 *
 * @author Dominik Hoftych
 */
class Matrix<T>(
    private val n: Int,
    private val m: Int,
    private val initializer: Matrix<T>.(row: Int, col: Int) -> T
) : Iterable<T> {

    val matrix: List<List<T>> = List(n) { row ->
        List(m) { col ->
            initializer(row, col)
        }
    }

    override operator fun iterator(): Iterator<T> = object : Iterator<T> {
        private var idx = 0

        override fun hasNext(): Boolean = idx < n * m

        override fun next(): T {
            if (!this.hasNext()) throw NoSuchElementException()
            return matrix[idx / n][idx++ % m]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix<*>

        if (n != other.n) return false
        if (m != other.m) return false
        if (matrix != other.matrix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = n
        result = 31 * result + m
        result = 31 * result + matrix.hashCode()
        return result
    }
}

/**
 * Returns the particular [Square] on given [position] of the matrix
 */
operator fun <T> Matrix<T>.get(position: Position): T = matrix[position.row][position.col]

/**
 * Performs the given [action] on each row of the matrix
 */
fun <T> Matrix<T>.forEachRow(action: (item: List<T>) -> Unit) {
    for (row in matrix) {
        action(row)
    }
}

/**
 * Performs the given [action] on each element, providing both row and column index with the element
 */
fun <T> Matrix<T>.forEachIndexed(action: (row: Int, col: Int, item: T) -> Unit) {
    for ((rowIdx, row) in matrix.withIndex()) {
        for ((colIdx, cell) in row.withIndex()) {
            action(rowIdx, colIdx, cell)
        }
    }
}
