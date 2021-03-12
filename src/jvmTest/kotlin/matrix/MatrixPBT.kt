package matrix

//import org.assertj.core.api.*
import net.jqwik.api.*
import kotlin.math.sqrt
import kotlin.test.assertEquals


//import org.junit.Assert.*
//import kotlin.test.*


internal class MatrixPBT {
    @Provide
    fun matrixMemberList(): Arbitrary<List<Int>>? {
        val matrixSizes = Arbitraries.integers().between(1, 6)
        val lists = Arbitraries.integers().between(-10, 10).list().ofSize(36)

        return Combinators.combine(matrixSizes, lists).`as` { size, list ->
            list.take(size * size)
        }
    }

    fun listToMatrix(l: List<Int>): Matrix {
        val n = sqrt(l.size.toFloat()).toInt()

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

    var ok  = 0

    var notok  = 0

    @Property
    fun LUPequalsA(@ForAll("matrixMemberList") l: List<Int>) {
        var A = listToMatrix(l)

        try {
            val (L, U, P) = A.decomposeLUP()
            assertEquals((P * A).roundToPrecision(), (L * U).roundToPrecision(),
                "Matrices: ${L.toStringFancy()} ${U.toStringFancy()} ${P.toStringFancy()} ")
            ok++
        } catch (e: LinearDependence) {
            notok++
            println(e.message)
            println("ok: $ok")
            println("notok: $notok")

        }
    }

    @Property
    fun inverseIsReversible(@ForAll("matrixMemberList") l: List<Int>) {
        var m = listToMatrix(l)

        /*try {
            val inv = m.inv()

            val I = identity(m.cols, m.mode)

            /*assertThat(I.toStringFancy()).withFailMessage(
                "Matrices: ${m.toStringFancy()} ${inv.toStringFancy()}"
            ).isEqualTo((m * inv).roundToPrecision().toStringFancy())
            assertThat(I.toStringFancy()).withFailMessage(
                "Matrices: ${inv.toStringFancy()} ${m.toStringFancy()}"
            ).isEqualTo((inv * m).roundToPrecision().toStringFancy())*/

            assertEquals(I, (m * inv).roundToPrecision(), "Matrices: ${m.toStringFancy()} ${inv.toStringFancy()}")
            assertEquals(I, (inv * m).roundToPrecision(), "Matrices: ${inv.toStringFancy()} ${m.toStringFancy()}")
        } catch (e: LinearDependence) {
            println(e.message)
        }*/

        m = m.toFractionMatrix()

        try {
            val inv = m.inv()

            val I = identity(m.cols, m.mode)

            assertEquals(I, (m * inv))
            assertEquals(I, (inv * m))
        } catch (e: LinearDependence) {
            println(e.message)
        }
    }
}