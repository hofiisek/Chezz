package board

/**
 * Wrapper to simplify declaration of a matrix as a list of lists.
 *
 * @param rows number of rows
 * @param cols number of cols
 * @param initializer lambda function to be used to initialize matrix elements
 *
 * @author Dominik Hoftych
 */
data class Matrix<T>(
        val rows: Int,
        val cols: Int,
        private val initializer: Matrix<T>.(row: Int, col: Int) -> T
) {

    val matrix: List<List<T>> = List(rows) { row ->
        List(cols) { col ->
            initializer(row, col)
        }
    }

    operator fun iterator(): Iterator<T> = object : Iterator<T> {
        var idx = 0

        override fun hasNext(): Boolean = idx < rows * cols

        override fun next(): T {
            return matrix[idx/rows][idx % cols].also { idx++ }
        }
    }
}

operator fun <T> Matrix<T>.get(idx: Int): List<T> = matrix[idx]

fun <T> Matrix<T>.forEach(block: Matrix<T>.(item: T) -> Unit) {
    for (item in this) {
        block(item)
    }
}

fun <T> Matrix<T>.forEachRow(block: Matrix<T>.(item: List<T>) -> Unit) {
    for (row in this.matrix) {
        block(row)
    }
}

fun <T> Matrix<T>.forEachIndexed(block: Matrix<T>.(row: Int, col: Int, item: T) -> Unit) {
    for ((rowIdx, matrixRow) in this.matrix.withIndex()){
        for ((colIdx, matrixCell) in matrixRow.withIndex()) {
            block(rowIdx, colIdx, matrixCell)
        }
    }
}