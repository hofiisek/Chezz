package board

/**
 * Simple matrix implementation internally represented as a list of lists.
 *
 * Extends and delegates to the [List] interface but provides adjusted iterator
 * so it can by easily iterated element by element just as it was a 1D array.
 *
 * Due to the custom iterator, the index provided by the well known [forEachIndexed(int, action)][List.forEachIndexed]
 * method behaves as it was a 1D array (thus ranging from 1 to rows*cols exclusive).
 *
 * To iterate through the matrix with both indices (row-wise and column-wise), please use
 * the [forEachIndexed(row, col, action)][forEachIndexed] method.
 *
 * @param rows number of rows
 * @param cols number of cols
 * @param initializer lambda function providing current row and column to initialize matrix elements
 *
 * @author Dominik Hoftych
 */
data class Matrix<T>(
        val rows: Int,
        val cols: Int,
        private val initializer: Matrix<T>.(row: Int, col: Int) -> T
) : List<T> by ArrayList() {

    val matrix: List<List<T>> = List(rows) { row ->
        List(cols) { col ->
            initializer(row, col)
        }
    }

    override operator fun iterator(): Iterator<T> = object : Iterator<T> {
        private var idx = 0

        override fun hasNext(): Boolean = idx < rows * cols

        override fun next(): T {
            return matrix[idx/rows][idx++ % cols]
        }
    }
}


/**
 * Extension function allowing to use [Position] as an index to the receiver [Matrix]
 */
operator fun <T> Matrix<T>.get(position: Position): T = matrix[position.row][position.col]

/**
 * Performs the given [action] on each row of the matrix.
 */
fun <T> Matrix<T>.forEachRow(action: Matrix<T>.(item: List<T>) -> Unit) {
    for (row in matrix) {
        action(row)
    }
}

/**
 * Performs the given [action] on each element, providing both row and column index with the element.
 */
fun <T> Matrix<T>.forEachIndexed(action: Matrix<T>.(row: Int, col: Int, item: T) -> Unit) {
    for ((rowIdx, matrixRow) in matrix.withIndex()){
        for ((colIdx, matrixCell) in matrixRow.withIndex()) {
            action(rowIdx, colIdx, matrixCell)
        }
    }
}