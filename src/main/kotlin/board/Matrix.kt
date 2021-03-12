package board

/**
 * Creates a new matrix of [n] rows and [m] cols and initializes each element
 * using the [initializer].
 *
 * Delegates to the [List] interface but provides adjusted iterator
 * so it can by easily iterated element by element just as it was a 1D array.
 *
 * Due to the custom iterator, the index provided by the [forEachIndexed(int, action)][List.forEachIndexed]
 * method behaves as it was a 1D array (thus ranging from 1 to rows*cols exclusive).
 * To iterate through the matrix with both indices (row-wise and column-wise), use
 * [forEachIndexed(row, col, action)][forEachIndexed] instead.
 *
 * @param n number of rows
 * @param m number of cols
 * @param initializer lambda function providing current row and column to initialize matrix elements
 *
 * @author Dominik Hoftych
 */
data class Matrix<T>(
    private val n: Int,
    private val m: Int,
    private val initializer: Matrix<T>.(row: Int, col: Int) -> T
) : List<T> by ArrayList() {

    val matrix: List<List<T>> = List(n) { row ->
        List(m) { col ->
            initializer(row, col)
        }
    }

    override operator fun iterator(): Iterator<T> = object : Iterator<T> {
        private var idx = 0

        override fun hasNext(): Boolean = idx < n * m

        override fun next(): T {
            return matrix[idx/n][idx++ % m]
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
fun <T> Matrix<T>.forEachRow(action: Matrix<T>.(item: List<T>) -> Unit) {
    for (row in matrix) {
        action(row)
    }
}

/**
 * Performs the given [action] on each element, providing both row and column index with the element
 */
fun <T> Matrix<T>.forEachIndexed(action: Matrix<T>.(row: Int, col: Int, item: T) -> Unit) {
    for ((rowIdx, row) in matrix.withIndex()){
        for ((colIdx, cell) in row.withIndex()) {
            action(rowIdx, colIdx, cell)
        }
    }
}