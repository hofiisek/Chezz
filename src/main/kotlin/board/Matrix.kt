package board

/**
 * Wrapper to simplify declaration of a matrix as a list of lists.
 *
 * @author Dominik Hoftych
 */
data class Matrix<T>(val rows: Int, val cols: Int, private val initializer: Matrix<T>.(row: Int, col: Int) -> T) {

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