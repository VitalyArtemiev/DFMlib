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

        a = when (mode) {
            MatrixMode.mDouble -> {
                Array2DDouble(rows, cols)
            }
            MatrixMode.mFraction -> {
                Array2DFraction(rows, cols)
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
            require(mode == other.mode) { "Matrix mode mismatch" }

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
        require(mode == m.mode) { "Matrix mode mismatch" }
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
        require(mode == m.mode) { "Matrix mode mismatch" }
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
        require(mode == m.mode) { "Matrix mode mismatch" }
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

    fun decomposeLUP(): Triple<Matrix, Matrix, Matrix> {
        require(cols == rows) { "Square matrix required" }

        val L = identity(rows, mode)

        var A = copy()

        val P = identity(rows, mode)

        for (n in 0 until rows) {
            if (A[n, n] == initNumber()) {
                var i = n + 1
                while (i < rows && A[i, n] == initNumber()) {
                    i++
                }

                if (i == rows) {
                    throw LinearDependence("Matrix cannot be decomposed:\n$this\n")
                } else {
                    P.swapRow(n, i)
                    A.swapRow(n, i)
                }
            }

            val Ln = identity(rows, mode)

            for (i in n+1 until rows) {
                val l = A[i, n] / A[n, n]

                Ln[i, n] = - l

                L[i, n] = l
            }
            A = Ln * A
        }

        val U = A

        return Triple(L, U, P)
    }

    fun det(): Number {
        check(cols == rows) { "Square matrix required" }

        var pZeroCount = 0

        var result = try {
            val (_, u, p) = decomposeLUP()

            var r = u[0, 0]
            for (i in 1 until cols) {
                if (p[i, i] == initNumber()) {
                    pZeroCount++ //counting number of zeros on main diagonal
                }
                r *= u[i, i]
            }

            if (pZeroCount / 2 % 2 != 0) { //number of permutations is half that the count of zeros
                r *= (-1)
            }

            r
        } catch (e: LinearDependence) {
            println(e.message)

            initNumber()
        }

        return if (mode == MatrixMode.mDouble) {
            if (result == -0.0) //apparently -0.0 != +0.0 but only sometimes
                result = 0.0

            (result as Double).round(precision)
        } else
            result as Fraction
    }

    fun inv(): Matrix {
        val N = rows
        val IA = Matrix(rows, cols, mode)

        val A = this
        val (L, U, P) = decomposeLUP()


        for (j in 0 until N) {
            for (i in 0 until N) {
                IA[i, j] = if (P[i, j] == initNumber(1)) initNumber(1) else initNumber()

                for (k in 0 until i)
                    IA[i, j] -= L[i, k] * IA[k, j]
            }
            for (i in N - 1 downTo 0) {
                for (k in i + 1 until N)
                    IA[i, j] -= U[i, k] * IA[k, j]

                IA[i, j] /= U[i, i]
            }
        }

        return IA
    }

    fun pinv() {

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

    fun roundToPrecision(): Matrix {
        if (mode == MatrixMode.mFraction){
            return this
        }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                this[i, j] = (this[i, j] as Double).round(precision)
                if (this[i, j] == -0.0) {
                    this[i, j] = 0.0
                }
            }
        }

        return this
    }
}

fun identity(size: Int, mode: MatrixMode): Matrix {
    val m = Matrix(size, size, mode)

    for (i in 0 until size) {
        m[i, i] = m.initNumber(1)
    }
    return m
}

fun forwardSubstitution(LT: Matrix, b: Matrix): Matrix {
    //todo: require(LT is lower triangular)
    val x = Matrix(b.rows, 1, b.mode)

    x[0, 0] = b[0, 0] / LT[0, 0]

    for (i in 1 until b.rows) {
        var s = LT.initNumber()
        for (j in 0 until i) {
            s += LT[i, j] * x[j, 0]
        }

        x[i, 0] = (b[i, 0] - s) / LT[i, i]
    }

    return x
}

fun backwardSubstitution(UT: Matrix, b: Matrix): Matrix {
    //todo: require(UT is upper triangular)
    val x = Matrix(b.rows, 1, b.mode)

    val m = b.rows - 1

    x[m, 0] = b[m, 0] / UT[m, m]

    for (i in m - 1 downTo 0) {
        var s = UT.initNumber()
        for (j in i until UT.cols) {
            s += UT[i, j] * x[j, 0]
        }

        x[i, 0] = (b[i, 0] - s) / UT[i, i]
    }

    return x
}

fun linSolve(A: Matrix, b: Matrix): Matrix {
    require(A.mode == b.mode) { "Matrix mode mismatch" }
    require(A.rows == b.rows) { "Matrix size mismatch" }
    require(A.rows == A.cols) { "Matrix size mismatch" }

    val (L, U, P) = A.decomposeLUP()

    val y = forwardSubstitution(L, P * b)
    val x = backwardSubstitution(U, y)

    return x
}