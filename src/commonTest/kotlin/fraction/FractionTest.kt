package fraction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class FractionTest {
    @Test
    fun overflowTest() {
        var f = Fraction(Int.MAX_VALUE, 1)
        f = f + 2
        assertEquals("${Int.MAX_VALUE + 2L}/1", f.toString())

        f = Fraction(1, Int.MAX_VALUE)
        f = f / 2
        assertEquals("1/${Int.MAX_VALUE * 2L}", f.toString())

        var f1 = Fraction(1, 5)
        var f2 = Fraction(1, 6) //looks like addsorted is borked
        assertEquals(f1, f1 + f2 - f2)
    }

    @Test
    fun test() {
        var f = Fraction(256, 14)

        assertEquals("128/7", f.toString(), "Fraction constructor failed")
        assertEquals("2*2*2*2*2*2*2/7", f.toStringFactorized(), "Fraction factors are wrong")

        f = Fraction(30, 30)
        assertTrue(f.equals(1))

        assertEquals(Fraction(42), Fraction(42, 1))
        assertEquals(Fraction(42), Fraction(84, 2))
        assertEquals(Fraction(42), Fraction(-84, -2))
        assertEquals(Fraction(-7), Fraction(-7, 1))
        assertEquals(Fraction(-7), Fraction(7, -1))

        var a = Fraction()
        assertEquals("0/1", a.toString(), "Fraction constructor failed")

        a += 1
        assertEquals("1/1", a.toString(), "Fraction sum failed")

        a *= 5
        assertEquals("5/1", a.toString(), "Fraction mult failed")

        a *= -1
        assertEquals("-5/1", a.toString(), "Fraction mult failed")

        f = a
        a *= 2
        assertTrue(f.equals(-5), "Equals for int is broken")

        a = Fraction(1, 1) * Fraction(1, 2) * Fraction(1, 1)
        assertEquals("1/2", a.toStringFactorized())

        a = Fraction(5, 2) * Fraction(1, 3)
        assertEquals("5/2*3", a.toStringFactorized())

        a = Fraction(5, 6)
        var b = a.copy()
        assertTrue(a.equals(b), "Equals with fraction is broken")

        f = Fraction(6, 5)
        var c = f.copy()

        a *= f
        assertEquals("1/1", a.toString(), "Multiplication by fraction is broken")

        assertEquals("6/5", f.toString(), "Multiplication by fraction is broken")

        b += c
        c = Fraction(61, 30)
        assertTrue(b.equals(c), "Sum with fraction is broken")

        c -= f
        assertEquals("5/6", c.toString(), "Fraction minus failed")

        a = Fraction(7, 6)
        b = a.copy()
        a /= b
        assertEquals("1/1", a.toString(), "Division by fraction error")

        b /= 7
        assertEquals("1/6", b.toString(), "Division by int error")

        a = Fraction()
        a *= b
        assertTrue(a.equals(0), "Mult by 0 failure")

        b *= a
        assertTrue(b.equals(0), "Mult by 0 failure")

        a = Fraction(125, 6)
        a.negate()
        assertEquals("-125/6", a.toString(), "Unary minus failed")

        b = Fraction(-5, 1)
        assertEquals("-5/1", b.toString(), "Problem with negative construction")

        a *= b
        assertEquals("625/6", a.toString(), "Problem with negative mult")
        assertEquals("5*5*5*5/2*3", a.toStringFactorized(), "Problem with factor string")

        a *= 0
        assertTrue(a.equals(0), "Mult by 0 failure")

        b = Fraction(234, -72)
        assertEquals("-13/4", b.toString(), "Problem with negative construction")

        var f1 = Fraction(65535, 20)
        assertEquals("13107/4", f1.toString(), "Fraction constructor failed")
        assertEquals("3*17*257/2*2", f1.toStringFactorized(), "Fraction factors are wrong")

        f1 = Fraction.valueOf("-501/85")
        assertEquals("-501/85", f1.toString())
        f1 = Fraction.valueOf("-501/-85")
        assertEquals("501/85", f1.toString())
        f1 = Fraction.valueOf("501/-85")
        assertEquals("-501/85", f1.toString())
        f1 = Fraction.valueOf("-501")
        assertEquals("-501/1", f1.toString())

        f1 -= 1
        assertEquals("-502/1", f1.toString())

        f1 = Fraction.valueOf("0")
        assertEquals("0/1", f1.toString())
        assertTrue(f1.isZero)

        f1 = Fraction(8, 6)
        f1 *= f1
        assertEquals("16/9", f1.toString(), "Multiply by self failed")

        f1 -= f1
        assertEquals("0/1", f1.toString(), "Subtract by self failed")

        a = Fraction(5, 6)

        b = Fraction(7, 8)

        c = -a
        assertEquals("-5/6", c.toString(), "")
        assertEquals("5/6", a.toString(), "")

        c = a + b
        assertEquals("41/24", c.toString(), "")
        assertEquals("5/6", a.toString(), "")
        assertEquals("7/8", b.toString(), "")
        assertEquals(a + b, b + a)

        c = a * b
        assertEquals("35/48", c.toString(), "")
        assertEquals("5/6", a.toString(), "")
        assertEquals("7/8", b.toString(), "")
        assertEquals(a * b, b * a)

        c = a - b
        assertEquals("-1/24", c.toString(), "")
        assertEquals("5/6", a.toString(), "")
        assertEquals("7/8", b.toString(), "")
        assertEquals(a - b, -b + a)

        c = a / b
        assertEquals("20/21", c.toString(), "")
        assertEquals("5/6", a.toString(), "")
        assertEquals("7/8", b.toString(), "")

        c = -c
        assertEquals("-20/21", c.toString(), "")
        c *= b
        c += a
        assertEquals("0/1", c.toString(), "")

        c = Fraction.valueOf("20/21")

        var d = -c
        d *= b
        d += a
        assertEquals("0/1", d.toString(), "")

        //System.out.println("a b c " + a.toString() + ' ' + b.toString() + ' ' + c.toString())

        c = a + b * -c
        assertEquals("0/1", c.toString(), "")

        c = Fraction.valueOf("20/21")
        d = b * -c
        assertEquals("-5/6", d.toString(), "")

        var e = d.copy()

        d = d + a
        assertEquals("0/1", d.toString(), "")

        e+= a
        assertEquals(d.toString(), e.toString(), "")

        a = Fraction.valueOf("6/5")
        b = Fraction.valueOf("13/10")
        c = Fraction.valueOf("3/5")
        c = (a - b) / c
        assertEquals("-1/6", c.toString(), "")

        a = Fraction(1, 2)
        b = Fraction(2, 3)
        c = Fraction(3, 4)
        d = Fraction(4, 5)
        var I = Fraction(1, 1)
        var O = Fraction()

        e = a * I + b * O
        assertEquals(a, e)
        e = I * a + O * b
        assertEquals(a, e)

        e = a * O + b * I
        assertEquals(b, e)
        e = O * a + I * b
        assertEquals(O * a, O)
        assertEquals(I * b, b)
        assertEquals(Fraction(2, 3), Fraction() + Fraction(2, 3))
        assertEquals(b, e)

        e = c * I + d * O
        assertEquals(c, e)
        e = I * c + O * d
        assertEquals(c, e)

        e = c * O + d * I
        assertEquals(d, e)
        e = O * c + I * d
        assertEquals(d, e)

        var dd: Double = 15.67
        //println(dd.toByte())
        //println(dd.toInt())
        //println(dd.toChar())

        a = Fraction(1, 1)

        b = a * a * a * a * a
        assertEquals("1/1", b.toStringFactorized())

        b = a / a / a / a / a
        assertEquals("1/1", b.toStringFactorized())

        a = Fraction(1, 2)

        b = a * a * a * a * a
        assertEquals("1/2*2*2*2*2", b.toStringFactorized())

        b = a / a / a / a / a
        assertEquals("2*2*2/1", b.toStringFactorized())
    }
}