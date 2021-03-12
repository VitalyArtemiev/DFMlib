package matrix

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

//Alternative way to test doubles to precision. Works only in JVM(((
internal class MatrixTestJVM {
    @Test
    fun testGaussDouble() {
        for (i in 1..6) {
            val m = randomMatrix(i, i, mode = MatrixMode.mDouble)
            try {
                val (l, u, p) = m.decomposeLUP()

                println("$l\n\n$u")

                for (j in 0 until i) {
                    assertEquals(1.0, l[j, j] as Double, 0.000000000001, "$l\nrow:$j col:$j")

                    for (k in 0 until j){
                        assertEquals(.0, u[j, k] as Double, 0.000000000001, "$u\nrow:$j col:$k")
                    }

                    for (k in j+1 until i){
                        assertEquals(.0, l[j, k] as Double, 0.000000000001, "$l\nrow:$j col:$k")
                    }
                }

                val result = (p * l * u).roundToPrecision()

                assertEquals(m, result, "l\n$l\nu\n$u\n")
            } catch (e: LinearDependence) {
                println(e.message)
                continue
            }
        }
    }
}