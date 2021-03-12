package matrix

//import org.assertj.core.api.*
import net.jqwik.api.*
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
    fun matrixPair(): Arbitrary<Pair<Matrix, Matrix>> {
        return Combinators.combine(matrix(), matrix()).`as` { m1, m2 -> Pair(m1, m2) }
    }

    @Property(tries = 10000)
    fun commutative(@ForAll("matrix") a: Matrix, @ForAll("matrix") b: Matrix) {
        if (a.rows == b.rows && a.cols == b.cols) {
            assertEquals(a, a + b - b)
            assertEquals(b, b - a + a)

            val fa = a.toFractionMatrix()
            val fb = b.toFractionMatrix()

            assertEquals(fa, fa + fb - fb)
            assertEquals(fb, fb - fa + fa)
        }
    }

    @Property
    fun LUPequalsA(@ForAll("squareMatrix") A: Matrix) {
        try {
            val (L, U, P) = A.decomposeLUP()
            assertEquals(
                (P * A).roundToPrecision(), (L * U).roundToPrecision(),
                "Matrices: ${L.toStringFancy()} ${U.toStringFancy()} ${P.toStringFancy()} "
            )
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