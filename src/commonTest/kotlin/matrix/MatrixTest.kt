package matrix

//import org.junit.jupiter.api.Assertions.*

import fraction.*
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

fun randomMatrix (rows: Int, cols: Int, seed: Int = -1, mode: MatrixMode = MatrixMode.mDouble): Matrix {
    val m = Matrix(rows, cols, mode)

    var seed = seed
    if (seed == -1) {
        seed = Random.nextInt()
    }

    for (i in 0 until rows)
        for (j in 0 until cols) {
            seed++
            m[i, j] = m.initNumber(Random(seed).nextInt(-10, 10))
        }

    return m
}

internal class MatrixTest {
    @Test
    fun stest() {
        val m = Matrix("1 2\n3 4")

        assertEquals(-2.0, m.det())

        val (l, u, p) = m.decomposeLUP()

        assertEquals(m, p * l * u)
    }

          @Test
    fun testGaussFraction () {
        for (i in 1..6) {
            val m = randomMatrix(i, i, mode = MatrixMode.mFraction)
            try {
                val (l, u, p) = m.decomposeLUP()

                println("$l\n\n$u\n\n$p\n")

                for (j in 0 until i) {
                    assertEquals(Fraction(1), l[j, j], "$l\nrow:$j col:$j")

                    for (k in 0 until j){
                        assertEquals(Fraction(), u[j, k], "$u\nrow:$j col:$k")
                    }

                    for (k in j+1 until i){
                        assertEquals(Fraction(), l[j, k], "$l\nrow:$j col:$k")
                    }
                }

                assertEquals(m, l * u * p, "fl\n$l\nfu\n$u\n")
            } catch (e: LinearDependence) {
                println(e.message)
                continue
            }
        }
    }

    @Test
    fun testGaussDouble () {
        for (i in 1..6) {
            val m = randomMatrix(i, i, mode = MatrixMode.mDouble)
            try {
                val (l, u, p) = m.decomposeLUP()

                val U = u.copy().roundToPrecision() //copy to avoid losing precision in original matrices
                val L = l.copy().roundToPrecision()

                for (j in 0 until i) {
                    assertEquals(1.0, L[j, j], "$L\nrow:$j col:$j")

                    for (k in 0 until j) {
                        assertEquals(.0, U[j, k], "$U\nrow:$j col:$k")
                    }

                    for (k in j + 1 until i) {
                        assertEquals(.0, L[j, k], "$L\nrow:$j col:$k")
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

    @Test
    fun testForwardSubstitution() {
        val a = Matrix("2 0\n2 1")
        val b = Matrix("3\n6")

        val x = forwardSubstitution(a, b)
        assertEquals(Matrix("1.5\n3.0"), x)
    }

    @Test
    fun testBackwardSubstitution() {
        val a = Matrix("1 2 \n0 1")
        val b = Matrix("3\n2")

        val x = backwardSubstitution(a, b)
        println(x)
    }

    @Test
    fun testLinSolve() {
        for (i in 1..6) {
            val A = randomMatrix(i, i, mode = MatrixMode.mDouble)
            val b = randomMatrix(i, 1, mode = MatrixMode.mDouble)

            val x = linSolve(A, b)

            assertEquals(b, (A * x).roundToPrecision())
        }

        for (i in 1..6) {
            val A = randomMatrix(i, i, mode = MatrixMode.mFraction)
            val b = randomMatrix(i, 1, mode = MatrixMode.mFraction)

            println(i)

            val x = linSolve(A, b)

            println(i)

            val result = A * x

            assertEquals(b, result)
            println(i)
        }
    }

    @Test
    fun testInverse() {
        for (i in 1..6) {
            val m = randomMatrix(i, i, mode = MatrixMode.mDouble)
            val I = identity(i, MatrixMode.mDouble)

            try {
                val inv = m.inv()

                assertEquals(I, (m * inv).roundToPrecision(), "M: ${m.toStringFancy()} inv: ${inv.toStringFancy()}")
                assertEquals(I, (inv * m).roundToPrecision(), "M: ${m.toStringFancy()} inv: ${inv.toStringFancy()}")
            } catch (e: LinearDependence) {
                print("linear dependence")
            }
        }


        for (i in 1..6) {
            val m = randomMatrix(i, i, mode = MatrixMode.mFraction)
            val I = identity(i, MatrixMode.mFraction)
            try {
                val inv = m.inv()

                assertEquals(I, m * inv, "M: ${m.toStringFancy()} inv: ${inv.toStringFancy()}")
                assertEquals(I, inv * m, "M: ${m.toStringFancy()} inv: ${inv.toStringFancy()}")
            } catch (e: LinearDependence) {
                print("linear dependence")
            }
        }
    }

    @Test
    fun testSerialization() {
        for (j in 1..6) {
            for (i in 1..6) {
                val m1 = randomMatrix(j, i, mode = MatrixMode.mDouble)
                val m2 = Matrix(m1.toString())

                assertEquals(m1, m2)
            }

            for (i in 1..6) {
                val m1 = randomMatrix(j, i, mode = MatrixMode.mFraction)
                val m2 = Matrix(m1.toString())

                assertEquals(m1, m2)
            }
        }
    }

    @Test
    fun testDoubleMatrix () {
        var m = Matrix(4, 5)
        assertEquals(4, m.rows)
        assertEquals(5, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)

        var s = "1.5"
        m = Matrix(s)
        assertEquals(1, m.rows)
        assertEquals(1, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])

        val a = m.a[0, 0] - 1.0
        assertEquals(0.5, a)

        s = "1.5 2.5"
        m = Matrix(s)
        assertEquals(1, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])

        s = "1.5\n" +
                "2.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(1, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[1, 0])

        s = "1.5\t2.5\n" +
                "3.5\t4.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[1, 0])
        assertEquals(4.5, m.a[1, 1])

        s = "1.5 2.5\n" +
                "3.5\t4.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[1, 0])
        assertEquals(4.5, m.a[1, 1])

        s = "-1.5 2.5\n" +
                "0.5\t-0.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(-1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(0.5, m.a[1, 0])
        assertEquals(-0.5, m.a[1, 1])

        s = "1.5 2.5\t3.5 4.5\n" +
                "5.5\t6.5 7.5\t8.5\n"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(4, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[0, 2])
        assertEquals(4.5, m.a[0, 3])
        assertEquals(5.5, m.a[1, 0])
        assertEquals(6.5, m.a[1, 1])
        assertEquals(7.5, m.a[1, 2])
        assertEquals(8.5, m.a[1, 3])

        s = "1.5 2.5\t3.5 4.5\n" +
                "5.5\t6.5 7.5\t8.5\n" +
                "9.5 10 -2 0 "
        m = Matrix(s)
        assertEquals(3, m.rows)
        assertEquals(4, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[0, 2])
        assertEquals(4.5, m.a[0, 3])
        assertEquals(5.5, m.a[1, 0])
        assertEquals(6.5, m.a[1, 1])
        assertEquals(7.5, m.a[1, 2])
        assertEquals(8.5, m.a[1, 3])
        assertEquals(9.5, m.a[2, 0])
        assertEquals(10.0,m.a[2, 1])
        assertEquals(-2.0,m.a[2, 2])
        assertEquals(0.0, m.a[2, 3])

        s = "1 2\t \n" +
                "3 4 \n" +
                "5 6\t \n\n\n"
        m = Matrix(s)
        assertEquals(3, m.rows)
        assertEquals(2, m.cols)

        var m1 = m - m
        assertEquals(3, m1.rows)
        assertEquals(2, m1.cols)
        assertEquals(0.0, m1.a[0, 0])
        assertEquals(0.0, m1.a[2, 1])
        assertEquals(1.0, m.a[0, 0])
        assertEquals(6.0, m.a[2, 1])

        m1 = m - m * 2.0
        assertEquals(3, m1.rows)
        assertEquals(2, m1.cols)
        assertEquals(-1.0, m1.a[0, 0])
        assertEquals(-6.0, m1.a[2, 1])
        assertEquals(1.0, m.a[0, 0])
        assertEquals(6.0, m.a[2, 1])

        m1 = m1 + 1.0
        assertEquals(3, m1.rows)
        assertEquals(2, m1.cols)
        assertEquals(0.0, m1.a[0, 0])
        assertEquals(-5.0, m1.a[2, 1])

        assertEquals(m, m + 0.0)
        assertEquals(m + 1.0, 1.0 + m)
        assertEquals(m1 * 2.0, 2.0 * m1)

        val I = identity(3, MatrixMode.mDouble)
        assertEquals(3, I.rows)
        assertEquals(3, I.cols)
        assertEquals(1.0, I[0, 0])
        assertEquals(1.0, I[1, 1])
        assertEquals(1.0, I[2, 2])
        assertEquals(0.0, I[0, 1])
        assertEquals(0.0, I[0, 2])
        assertEquals(0.0, I[2, 1])
        assertEquals(m, I * m)

        assertEquals(m, m * identity(2, MatrixMode.mDouble))

        m = Matrix("1 2 3\n4 5 6")
        m1 = Matrix("1 2\n3 4\n5 6")
        var m2 = Matrix("22 28\n49 64")
        assertEquals(m2, m * m1)

        m = Matrix("-1\n4\n0")
        m1 = Matrix("1 2 -3")
        m2 = Matrix("-1 -2 3\n4 8 -12\n0 0 0")
        assertEquals(m2, m * m1)

        m1 = Matrix(m2.toString())
        assertEquals(m2, m1)

        m2.swapRow(1, 2)
        assertEquals("-1.0 -2.0 3.0\n0.0 0.0 0.0\n4.0 8.0 -12.0", m2.toString())
        m2.swapRow(0, 1)
        assertEquals("0.0 0.0 0.0\n-1.0 -2.0 3.0\n4.0 8.0 -12.0", m2.toString())

        var fm = I.toFractionMatrix()
        assertEquals("1/1 0/1 0/1\n0/1 1/1 0/1\n0/1 0/1 1/1", fm.toString())

        fm = m2.toFractionMatrix()
        assertEquals("0/1 0/1 0/1\n-1/1 -2/1 3/1\n4/1 8/1 -12/1", fm.toString())

        assertEquals(I, I.decomposeLUP().first)
        assertEquals(I, I.decomposeLUP().second)

        var det = I.det()
        assertEquals(1.0, det)

        m = Matrix("1 2 3\n4 5 6\n7 8 9")
        det = m.det()

        /*var d1: Double = 0.0 //THE WTF BLOCK
        var d2: Double = -0.0
        assertTrue(0.0 == -0.0)
        assertTrue(d1 == d2)
        assertTrue(d1.equals(d2))
        assertEquals(d1, d2)
        assertTrue(0.0.equals(-0.0))
        assertEquals(0.0, -0.0)*/

        assertEquals(0.0, det)

        m1 = Matrix("1 2 4\n4 5 6\n7 8 9")
        det = m1.det()
        assertEquals(-3.0, det)

        m1 = Matrix("1 2 4\n4 -5 6\n7 8 9")
        det = m1.det()

        val d = 187.0
        assertEquals(d, det)

        m1 = Matrix("1 2\n3 4")
        det = m1.det()

        assertEquals(-2.0, det)

        m1 = Matrix(
            "1\t2\t3\t4\t5\t6\n" +
                    "-1\t-2\t3\t5\t4\t3\n" +
                    "4\t8\t-12\t1\t3\t2\n" +
                    "4\t4\t3\t2\t41\t1\n" +
                    "9\t56\t8\t3\t1\t4\n" +
                    "0\t5\t7\t34\t2\t6"
        )
        det = m1.det()

        assertEquals(4369152.0, det)


        assertEquals(I, I.transpose())
        assertEquals(m, m.transpose().transpose())
        assertEquals("1.0 4.0 7.0\n2.0 5.0 8.0\n3.0 6.0 9.0", m.transpose().toString())

        m = Matrix("-1\n4\n0")
        m1 = Matrix("-1 4 0")
        assertEquals(m, m1.transpose())

        m = Matrix("1 2 3 4\n5 6 7 8")
        m1 = Matrix("1 5\n2 6\n3 7\n4 8")
        assertEquals(m, m1.transpose())
        assertEquals(m, m.transpose().transpose())
    }

    @Test
    fun testFractionMatrix () {
        var m = Matrix("1/1 2/1\t\t\n3/1\t4/1  ")
        var I = identity(2, MatrixMode.mFraction)
        assertEquals("1/1 0/1\n0/1 1/1", I.toString())
        assertEquals(m, m * I)

        m = Matrix("1/2 2/3\n3/4 4/5")
        assertEquals(m, m * I)

        assertEquals("3/4 13/15\n39/40 57/50", (m * m).toString())

        assertEquals("3/2 2/3\n3/4 9/5", (m + I).toString())

        assertEquals("-1/2 2/3\n3/4 -1/5", (m - I).toString())

        m = Matrix("1/2 2/3 3/4\n4/5 5/6 6/7")
        var m1 = Matrix(m.toString())
        assertEquals(m, m1)

        I = identity(3, MatrixMode.mFraction)
        assertEquals("1/1 0/1 0/1\n0/1 1/1 0/1\n0/1 0/1 1/1", I.toString())
        assertEquals(m, m * I)

        m = I + Fraction(1)
        assertEquals("2/1 1/1 1/1\n1/1 2/1 1/1\n1/1 1/1 2/1", m.toString())

        m = I - Fraction(1)
        assertEquals("0/1 -1/1 -1/1\n-1/1 0/1 -1/1\n-1/1 -1/1 0/1", m.toString())

        assertEquals(I + Fraction(1), Fraction(1) + I)
        assertEquals(I - Fraction(1), -Fraction(1) + I)
        assertEquals(Fraction(1) - I, -I + Fraction(1))
        assertEquals(-Fraction(1) - I, -I - Fraction(1))

        m1 = Matrix(
            "1/1\t2\t3\t4\t5\t6\n" +
                    "-1\t-2\t3\t5\t4\t3\n" +
                    "4\t8\t-12\t1\t3\t2\n" +
                    "4\t4\t3\t2\t41\t1\n" +
                    "9\t56\t8\t3\t1\t4\n" +
                    "0\t5\t7\t34\t2\t6"
        )
        val det = m1.det()

        assertEquals(Fraction(4369152, 1), det)
    }

    @Test
    fun zeroTest() {
        val m1 = Matrix("0 1\n1 0")
        val (L, U, P) = m1.decomposeLUP()
        assertEquals(m1, P * L * U)

        val I = identity(m1.rows, m1.mode)
        assertEquals(I, m1 * m1.inv())
    }

    @Test
    fun oneTest() {
        val A = Matrix("1 0 0 0\n0 0 0 1\n0 0 1 0\n1 1 0 0")
        val (L, U, P) = A.decomposeLUP()

        println("Matrices: L ${L.toStringFancy()} U ${U.toStringFancy()} P ${P.toStringFancy()} A ${A.toStringFancy()} ")

        val LU = L * U
        println("Matrices: LU ${LU.toStringFancy()}")

        val result = P * L * U

        println("Matrices: result ${result.toStringFancy()}")

        /*assertEquals(m1, L * U * P)

        val I = identity(m1.rows, m1.mode)
        val inv = m1.inv()

        assertEquals(I, m1 * inv)*/
    }

    @Test
    fun twoTest() {
        val m1 = Matrix("0 1\n1 1")
        val (L, U, P) = m1.decomposeLUP()

        assertEquals(m1, P * L * U)

        val I = identity(m1.rows, m1.mode)

        //assertEquals(I, m1 * m1.inv())
    }

    @Test
    fun threeTest() {
        val m1 = Matrix("0 0 1\n1 0 0\n0 1 0")
        val m2 = Matrix("0 1 0\n0 0 1\n1 0 0")
        val m = randomMatrix(3, 3)
        assertEquals(m * m1, m2 * m)

        val (L, U, P) = m1.decomposeLUP()

        //assertEquals(m1, L * U * P)

        val I = identity(m1.rows, m1.mode)

        //assertEquals(I, m1 * m1.inv())
    }
}