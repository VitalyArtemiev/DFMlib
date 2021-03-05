package matrix

operator fun Number.minus(a: Matrix): Matrix {
    return  - a + this
}

operator fun Number.plus(a: Matrix): Matrix {
    return  a + this
}

operator fun Number.times(a: Matrix): Matrix {
    return a * this
}

fun String.toMatrix(): Matrix {
    return Matrix(this)
}

