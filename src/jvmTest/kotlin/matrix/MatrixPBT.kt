package matrix

//import org.assertj.core.api.*
import fraction.isZero
import fraction.unaryMinus
import net.jqwik.api.*
import net.jqwik.api.constraints.IntRange
import kotlin.math.sqrt
import kotlin.test.assertEquals

//import org.junit.Assert.*
//import kotlin.test.*

@PropertyDefaults(afterFailure = AfterFailureMode.SAMPLE_FIRST)
internal class MatrixPBT {
    @Provide
    fun matrix(): Arbitrary<Matrix> {
        val matrixRows = Arbitraries.integers().between(1, 6)
        val matrixCols = Arbitraries.integers().between(1, 6)

        val lists = Arbitraries.integers().between(-10, 10).list().ofSize(36)
        var dirtyHack = 0
        return Combinators.combine(matrixRows, matrixCols, lists).`as` { rows, cols, list ->
            dirtyHack = cols
            list.take(rows * cols)
        }.map { list -> listToMatrix(list, dirtyHack) }
    }

    @Provide
    fun squareMatrix(): Arbitrary<Matrix> {
        val matrixSizes = Arbitraries.integers().between(1, 6)
        val lists = Arbitraries.integers().between(-10, 10).list().ofSize(36)

        return Combinators.combine(matrixSizes, lists).`as` { size, list ->
            list.take(size * size)
        }.map { list -> listToMatrix(list) }
    }

    @Provide
    fun matrixPairSameSize(): Arbitrary<Pair<Matrix, Matrix>> {
        val matrixRows = Arbitraries.integers().between(1, 6)
        val matrixCols = Arbitraries.integers().between(1, 6)

        val lists = Arbitraries.integers().between(-10, 10).list().ofSize(36 * 2)
        var curCols = 0
        var curRows = 0
        return Combinators.combine(matrixRows, matrixCols, lists).`as` { rows, cols, list ->
            curCols = cols
            curRows = rows
            list.take(rows * cols * 2)
        }.map { list ->
            Pair(
                listToMatrix(list.take(curCols * curRows), curCols),
                listToMatrix(list.takeLast(curCols * curRows), curCols)
            )
        }
    }

    @Property
    fun commutative(@ForAll("matrixPairSameSize") p: Pair<Matrix, Matrix>) {
        val (a, b) = p
        assertEquals(a, a + b - b)
        assertEquals(b, b - a + a)

        val fa = a.toFractionMatrix()
        val fb = b.toFractionMatrix()

        assertEquals(fa, fa + fb - fb)
        assertEquals(fb, fb - fa + fa)
    }

    @Property
    fun distributive(@ForAll("matrixPairSameSize") p: Pair<Matrix, Matrix>) {
        val (a, b) = p
        assertEquals(a + b, b + a)
        assertEquals(-a + b, b - a)

        val fa = a.toFractionMatrix()
        val fb = b.toFractionMatrix()

        assertEquals(fa + fb, fb + fa)
        assertEquals(-fa + fb, fb - fa)
    }

    @Property
    fun transposeIsReversible(@ForAll("matrix") m: Matrix) {
        assertEquals(m, m.transpose().transpose())

        val f = m.toFractionMatrix()
        assertEquals(f, f.transpose().transpose())
    }

    @Property
    fun swapRowIsReversible(
        @ForAll("matrix") m: Matrix,
        @ForAll @IntRange(min = 0, max = 5) i1: Int,
        @ForAll @IntRange(min = 0, max = 5) i2: Int
    ) {
        val row1 = i1 % m.rows
        val row2 = i2 % m.rows
        val o = m.copy()
        m.swapRow(row1, row2)
        m.swapRow(row1, row2)
        assertEquals(o, m)
    }

    @Property
    fun pTransposeIsInverse(@ForAll("squareMatrix") A: Matrix) {
        val I = identity(A.cols, A.mode)
        try {
            val (L, U, P) = A.decomposeLUP()
            assertEquals(
                I, P * P.transpose(),
                "Matrices: L ${L.toStringFancy()} U ${U.toStringFancy()} P ${P.toStringFancy()} A ${A.toStringFancy()} "
            )
        } catch (e: LinearDependence) {
            println(e.message)
        }
    }

    @Property
    fun LUPequalsA(@ForAll("squareMatrix") A: Matrix) {
        try {
            val (L, U, P) = A.decomposeLUP()
            assertEquals(
                A, (P.transpose() * L * U).roundToPrecision(),
                "Matrices: L ${L.toStringFancy()} U ${U.toStringFancy()} P ${P.toStringFancy()} A ${A.toStringFancy()} "
            )
        } catch (e: LinearDependence) {
            println(e.message)
        }
    }

    @Property
    fun swapRowChangesDetSign(
        @ForAll("squareMatrix") A: Matrix,
        @ForAll @IntRange(min = 0, max = 5) i1: Int,
        @ForAll @IntRange(min = 0, max = 5) i2: Int
    ) {
        try {
            val row1 = i1 % A.rows
            val row2 = i2 % A.rows
            val d1 = A.det()
            val original = A.copy()
            A.swapRow(row1, row2)
            val d2 = A.det()
            if (d1.isZero() || A.rows == 1 || row1 == row2) {
                assertEquals(
                    d1, d2, "Matrices: ${printMatrices(original, A)}\n Det1: $d1 ; Det2: $d2"
                )
            } else {
                assertEquals(
                    -d1, d2, "Matrices: ${printMatrices(original, A)}\n Det1: $d1 ; Det2: $d2"
                )
            }
        } catch (e: LinearDependence) {
            println(e.message)
        }
    }

    @Property
    fun inverseIsReversible(@ForAll("squareMatrix") m: Matrix) {
        var m = m

        try {
            val inv = m.inv()

            val I = identity(m.cols, m.mode)

            assertEquals(I, (m * inv).roundToPrecision(), "Matrices: ${m.toStringFancy()} ${inv.toStringFancy()}")
            assertEquals(I, (inv * m).roundToPrecision(), "Matrices: ${inv.toStringFancy()} ${m.toStringFancy()}")
        } catch (e: LinearDependence) {
            println(e.message)
        }

//        m = m.toFractionMatrix()
//
//        try {
//            val inv = m.inv()
//
//            val I = identity(m.cols, m.mode)
//
//            assertEquals(I, (m * inv))
//            assertEquals(I, (inv * m))
//        } catch (e: LinearDependence) {
//            println(e.message)
//        }
    }
}

fun listToMatrix(l: List<Int>, cols: Int = 0): Matrix {
    var n = cols
    if (n == 0) {
        n = sqrt(l.size.toFloat()).toInt()
    }

    var lines = l.chunked(n)

    var s = ""

    lines.first().forEach { int -> s += "$int " }
    lines = lines.drop(1)

    for (line in lines) {
        s += '\n'
        line.forEach { int -> s += "$int " }
    }

    return Matrix(s)
}