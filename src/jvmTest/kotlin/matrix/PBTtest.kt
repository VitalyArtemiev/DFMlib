package matrix

//import org.assertj.core.api.*
import net.jqwik.api.*
import org.assertj.core.api.Assertions.assertThat
import kotlin.math.sqrt
import kotlin.test.assertEquals


//import org.junit.Assert.*
//import kotlin.test.*


internal class PropertyBasedTests {
    @Property
    fun absoluteValueOfAllNumbersIsPositive(@ForAll anInteger: Int): Boolean {
        return Math.abs(anInteger) >= 0
    }

    @Property
    fun lengthOfConcatenatedStringIsGreaterThanLengthOfEach(
        @ForAll string1: String, @ForAll string2: String
    ) {
        val conc = string1 + string2

        assertThat(conc.length).isGreaterThan(string1.length)
        assertThat(conc.length).isGreaterThan(string2.length)
    }

    @Provide
    fun matrixMemberList(): Arbitrary<List<Int>>? {
        val matrixSizes = Arbitraries.integers().between(1, 6)
        val lists = Arbitraries.integers().between(-30, 30).list().ofSize(36)

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

    @Property
    fun inverseIsReversible(@ForAll("matrixMemberList") l: List<Int>) {
        var m = listToMatrix(l)

        /*try {
            val inv = m.inv()

            val I = identity(m.cols, m.mode)

            assertEquals(I, (m * inv).roundToPrecision())
            assertEquals(I, (inv * m).roundToPrecision())
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