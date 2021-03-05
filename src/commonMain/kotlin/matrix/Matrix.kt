package matrix

import fraction.*
import kotlin.math.pow

enum class MatrixMode { mDouble, mFraction }

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
                    throw Exception("Invalid matrix: malformed columns")

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
                    throw Exception("Invalid matrix: malformed columns")

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

    fun gauss(): Pair<Matrix, Matrix> {
        //val L = Matrix(rows, cols, mode)
        val U = Matrix(rows, cols, mode)

        var A = copy()

        var Ln = identity(rows, mode)
        for (n in 0 until rows) {
            for (i in n+1 until rows) {
                Ln[i, n] = - A[i,n] / A[n, n]
            }
        }

        val L = A

        return Pair(L, U)
    }

    fun lud(): Pair<Matrix, Matrix> {
        check(cols == rows) { "Square matrix required" }

        val l = Matrix(rows, cols, mode)
        val u = Matrix(rows, cols, mode)

        for (i in 0 until rows) {
            // Upper Triangular
            for (k in i until rows) {

                // Summation of L(i, j) * U(j, k)
                var sum = initNumber()
                for (j in 0 until i)
                    sum += l[i, j] * u[j, k]

                // Evaluating U(i, k)
                u[i, k] = a[i, k] - sum
            }

            // r Triangular
            for (k in i until rows) {
                if (i == k)
                    l[i, i] = initNumber(1) // Diagonal as 1
                else {

                    // Summation of L(k, j) * U(j, i)
                    var sum = initNumber()
                    for (j in 0 until i)
                        sum += l[k, j] * u[j, i]

                    // Evaluating L(k, i)
                    l[k, i] = (a[k, i] - sum) / u[i, i]
                }
            }
        }

        return Pair(l, u)
    }

    fun LUDecompose(): Pair<Matrix, Matrix> {
        val u = Matrix(rows, cols, mode)
        val l = Matrix(rows, cols, mode)

        for (i in 0 until l.rows) {
            u.a[0, i] = a[0, i]
            l.a[i, i] = initNumber(1)
        }

        for (i in 0 until u.rows) {
            for (j in 0 until u.rows) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += l.a[i, k] * u.a[k, j]
                }
                u.a[i, j] = a[i, j] - s
            }

            for (j in i + 1 until u.rows) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += l.a[j, k] * u.a[k, i]
                }

                if (!u.a[i, i].isZero())
                    l.a[j, i] = (a[j, i] - s) / u.a[i, i]
                else
                    l.a[j, i] = initNumber(1) //???
            }
        }

        return Pair(l, u)
    }

    /* INPUT: A - array of pointers to rows of a square matrix having dimension N
 *        Tol - small tolerance number to detect failure when the matrix is near degenerate
 * OUTPUT: Matrix A is changed, it contains a copy of both matrices L-E and U as A=(L-E)+U such that P*A=L*U.
 *        The permutation matrix is not stored as a matrix, but in an integer vector P of size N+1
 *        containing column indexes where the permutation matrix has "1". The last element P[N]=S+N,
 *        where S is the number of row exchanges needed for determinant computation, det(P)=(-1)^S
 */
    fun LUPDecompose(tolerance: Double = 0.01): Matrix {
        check(rows == cols)
        val result = this.copy()

        val permutations = Array<Int>(rows + 1) { 0 }

        //int i, j, k, imax;
        //double maxA, *ptr, absA;

        for (i in 0..rows) {
            permutations[i] = i //Unit permutation matrix, permutations[rows] initialized with rows
        }

        for (i in 0 until rows) {
            var maxA = initNumber()
            var imax = i

            for (k in i until rows) {
                var absA = abs(a[k, i])
                if (absA > maxA) {
                    maxA = absA
                    imax = k
                }
            }

            //require(true)
            //check(true)

            check(maxA > tolerance) { "Matrix appears to be degenerate: $maxA" }

            if (imax != i) {
                //pivoting permutations
                var j = permutations[i]
                permutations[i] = permutations[imax]
                permutations[imax] = j

                //pivoting rows of a
                result.a[i] = a[imax]
                result.a[imax] = a[i]

                //counting pivots starting from rows (for determinant)
                permutations[rows]++
            }

            for (j in i + 1 until rows) {
                result.a[j, i] /= result.a[i, i]

                for (k in i + 1 until rows)
                    result.a[j, k] -= result.a[j, i] * result.a[i, k]
            }
        }

        return result
    }

/* INPUT: A,P filled in LUPDecompose; b - rhs vector; N - dimension
 * OUTPUT: x - solution vector of A*x=b
 */
    /*void LUPSolve(double **A, int *P, double *b, int N, double *x) {

        for (int i = 0; i < N; i++) {
            x[i] = b[P[i]];

            for (int k = 0; k < i; k++)
            x[i] -= A[i][k] * x[k];
        }

        for (int i = N - 1; i >= 0; i--) {
            for (int k = i + 1; k < N; k++)
            x[i] -= A[i][k] * x[k];

            x[i] = x[i] / A[i][i];
        }
    }

*//* INPUT: A,P filled in LUPDecompose; N - dimension
 * OUTPUT: IA is the inverse of the initial matrix
 *//*
    void LUPInvert(double **A, int *P, int N, double **IA) {

        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
            if (P[i] == j)
                IA[i][j] = 1.0;
            else
                IA[i][j] = 0.0;

            for (int k = 0; k < i; k++)
            IA[i][j] -= A[i][k] * IA[k][j];
        }

            for (int i = N - 1; i >= 0; i--) {
            for (int k = i + 1; k < N; k++)
            IA[i][j] -= A[i][k] * IA[k][j];

            IA[i][j] = IA[i][j] / A[i][i];
        }
        }
    }

*//* INPUT: A,P filled in LUPDecompose; N - dimension.
 * OUTPUT: Function returns the determinant of the initial matrix
 *//*
    double LUPDeterminant(double **A, int *P, int N) {

        double det = A[0][0];

        for (int i = 1; i < N; i++)
        det *= A[i][i];

        if ((P[N] - N) % 2 == 0)
            return det;
        else
            return -det;
    }*/

    fun det(): Number {
        check(cols == rows) { "Square matrix required" }
        val a: Matrix = copy()

        for (i in 0 until a.cols)
        //this ignores extra rows
        {
            for (j in i until a.cols) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += a.a[i, k] * a.a[k, j]
                }
                a.a[i, j] = a.a[i, j] - s
            }

            for (j in i + 1 until a.cols) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += a.a[j, k] * a.a[k, i]
                }

                if (!a.a[i, i].isZero())
                    a.a[j, i] = (a.a[j, i] - s) / a.a[i, i]
                else
                    a.a[j, i] = initNumber()
            }
        }

        var result = initNumber(1)

        for (i in 0 until a.cols) {
            result *= a.a[i, i]
        }

        println(a.toString())
        println()

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