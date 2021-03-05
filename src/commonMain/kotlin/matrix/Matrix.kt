package matrix

import fraction.*
import kotlin.math.pow

enum class MatrixMode { mDouble, mFraction }

open class MatrixException(msg: String): Exception(msg)
class LinearDependence(msg: String): MatrixException(msg)

class Matrix {
    var a: Array2D
    var mode: MatrixMode
    var rows: Int
    var cols: Int

    var precision = 100000

    constructor(r: Int, c: Int, m: MatrixMode = MatrixMode.mDouble) {
        rows = r
        cols = c
        mode = m

        when (mode) {
            MatrixMode.mDouble -> {
                a = Array2DDouble(rows, cols)
            }
            MatrixMode.mFraction -> {
                a = Array2DFraction(rows, cols)
            }
        }
    }

    constructor(s: String) {
        val lines = s.dropLastWhile { it == '\n' }.split("\n")
        rows = lines.size
        cols = lines[0].dropLastWhile { it == '\t' || it == ' ' }.split(" ", "\t").size

        if (s.contains("/")) {
            mode = MatrixMode.mFraction
            a = Array2DFraction(rows, cols)

            for ((i, line) in lines.withIndex()) {
                val elements = line.dropLastWhile { it == '\t' || it == ' ' }.split(" ", "\t")

                if (elements.size != cols)
                    throw MatrixException("Invalid matrix: malformed columns")

                for ((j, element) in elements.withIndex()) {
                    a[i, j] = Fraction.valueOf(element)
                }
            }
        } else {
            mode = MatrixMode.mDouble
            a = Array2DDouble(rows, cols)

            for ((i, line) in lines.withIndex()) {
                val elements = line.dropLastWhile { it == '\t' || it == ' ' }.split(" ", "\t")

                if (elements.size != cols)
                    throw MatrixException("Invalid matrix: malformed columns")

                for ((j, element) in elements.withIndex()) {
                    a[i, j] = element.toDouble()
                }
            }
        }
    }

    override operator fun equals(other: Any?): Boolean {
        return if (other is Matrix) {
            if (other.rows != rows || other.cols != cols)
                return false

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    if (other.a[i, j] != a[i, j]) {
                        return false
                    }
                }
            }
            true
        } else
            false
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    operator fun get(i: Int): Any {
        return a[i]
    }

    operator fun get(i: Int, j: Int): Number {
        return a[i, j]
    }

    operator fun set(i: Int, j: Int, v: Any) {
        a[i, j] = v as Number
    }

    fun swapRow(r1: Int, r2: Int) {
        val t = a[r2]
        a[r2] = a[r1]
        a[r1] = t
        /*for i= 0 to Col - 1 do
  a[r1, i]*= -1; //change sign so to not change det  */
    }

    fun copy(): Matrix {
        val result = Matrix(rows, cols, mode)

        result.a = a.copy()
        return result
    }

    override fun toString(): String {
        var s = ""

        for (i in 0 until rows) {
            for (j in 0 until cols)
                s += a[i, j].toString() + ' '
            s = s.dropLast(1)

            if (i != rows - 1)
                s += '\n'.toString()
        }
        return s
    }

    fun toFractionMatrix(): Matrix {
        if (mode == MatrixMode.mFraction)
            return copy()

        val fm = Matrix(rows, cols, MatrixMode.mFraction)

        for (i in 0 until rows)
            for (j in 0 until cols) {
                val iPart = (a[i, j] as Double).toInt()
                var fPart = a[i, j] as Double - iPart//todo: write own number operator?
                if (fPart == 0.0) {
                    fm.a[i, j] = Fraction(iPart, 1)
                } else {
                    var k = 0
                    while (fPart != fPart.toInt().toDouble()) {
                        fPart *= 10
                        k++
                    }

                    val den: Int = 10.0.pow(k).toInt()
                    val num = fPart.toInt() + den * iPart

                    fm.a[i, j] = Fraction(num, den)
                }
            }
        return fm
    }

    operator fun unaryMinus(): Matrix {
        val result = Matrix(rows, cols, mode)

        for (i in 0 until result.rows) {
            for (j in 0 until result.cols) {
                result.a[i, j] = -a[i, j]
            }
        }
        return result
    }

    operator fun plus(m: Matrix): Matrix {
        require(rows == m.rows && cols == m.cols) { "Matrix size mismatch during sum" }

        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] += m[i, j]
            }
        return result
    }

    operator fun plus(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] += n
            }
        return result
    }

    operator fun minus(m: Matrix): Matrix {
        require(rows == m.rows && cols == m.cols) { "Matrix size mismatch during sum" }

        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] -= m[i, j]
            }
        return result
    }

    operator fun minus(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] -= n
            }
        return result
    }

    operator fun times(m: Matrix): Matrix {
        require(cols == m.rows) { "Matrix dimension mismatch" }

        val result = Matrix(rows, m.cols, mode)

        for (i in 0 until result.rows) {
            for (j in 0 until result.cols) {
                result[i, j] = initNumber()
                for (k in 0 until cols) {
                    result[i, j] += a[i, k] * m[k, j]
                }
            }
        }

        return result
    }

    operator fun times(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols)
                result.a[i, j] *= n

        return result
    }

    fun initNumber(v: Int = 0): Number {
        return when (mode) {
            MatrixMode.mDouble -> {
                v.toDouble() //as Double - no cast needed
            }
            MatrixMode.mFraction -> {
                Fraction(v, 1)
            }
        }
    }

    fun decomposeLU(): Pair<Matrix, Matrix> {
        require(cols == rows) { "Square matrix required" }

        var L = identity(rows, mode)

        var A = copy()

        for (n in 0 until rows) {
            val Ln = identity(rows, mode)

            if (A[n, n] == initNumber()) {
                println("ACHTUNG")

                var i = n + 1
                while (i < rows && A[i, n] == initNumber()) {
                    i++
                }

                if (i <= rows) {
                    throw LinearDependence("Matrix cannot be decomposed")
                } else {
                    A.swapRow(n, i)
                }
            }

            for (i in n+1 until rows) {
                println(i)
                println(A[n, n])
                println()

                check(A[n,n] != 0)

                Ln[i, n] = - A[i, n] / A[n, n]
            }

            println("L$n")

            println(Ln.toString())

            println("A")

            A = Ln * A

            println(A.toString())

            println()
            L = L * Ln
        }

        val U = A

        return Pair(L, U)
    }

    fun det(): Number {
        check(cols == rows) { "Square matrix required" }

        val (l, u) = decomposeLU()

        var result = u[0,0]
        for (i in 1 until cols) {
            result *= u[i,i]
        }

        return if (mode == MatrixMode.mDouble) {
            if (result == -0.0) //apparently -0.0 != +0.0 but only sometimes
                result = 0.0

            roundP(result as Double, precision)
        } else
            result as Fraction
    }

    fun transpose(): Matrix {
        val result = Matrix(cols, rows)

        for (i in 0 until result.rows) {
            for (j in 0 until result.cols) {
                result.a[i, j] = a[j, i]
            }
        }
        return result
    }
}

fun identity(size: Int, mode: MatrixMode): Matrix {
    val m = Matrix(size, size, mode)

    for (i in 0 until size) {
        m[i, i] = m.initNumber(1)
    }
    return m
}