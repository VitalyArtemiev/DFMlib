package fraction

import net.jqwik.api.*
import net.jqwik.api.constraints.IntRange
import net.jqwik.api.constraints.LongRange
import net.jqwik.api.constraints.Positive
import org.junit.jupiter.api.Assertions.*
import kotlin.math.absoluteValue

@PropertyDefaults(afterFailure = AfterFailureMode.SAMPLE_FIRST)
internal class FractionPBT {
    @Provide
    fun fraction1(): Arbitrary<Fraction> =
        Combinators.combine(Arbitraries.integers().between(-100, 100),
            Arbitraries.integers().between(-100, 100).filter { i -> i != 0 })
            .`as` { a, b -> Fraction(a, b) }

    @Property(seed = "1234567")
    fun factorizeIsCorrect(@ForAll @LongRange(min = 2, max = Long.MAX_VALUE) i: Long) {
        val arr = factorize(i).toArray()

        val l = IntList(* arr.toIntArray())
        val prod = l.product()
        assertEquals(i, prod, "$i : ${arr.toList()} = $prod\n${factorCache}")
    }
    @Property(seed = "1234567")
    fun factorizeCachedIsCorrect(@ForAll @LongRange(min = 2, max = Long.MAX_VALUE) i: Long) {
        val arr = factorizeCached(i)

        val l = IntList(* arr)
        val prod = l.product()

        assertEquals(i, prod, "$i : ${arr.toList()} = $prod\n${factorCache}")
    }

    @Property
    fun cachedFactorizeIsIdentical(@ForAll @LongRange(min = 2, max = 10000000) i: Long) {
        val expected = factorize(i).toArray().toIntArray()
        val actual = factorizeCached(i.absoluteValue)

        assertArrayEquals(expected, actual, "$i:   ${expected.toList()}    ${actual.toList()}\n${factorCache}")
    }

    @Property
    fun sumProps(@ForAll("fraction1") f1: Fraction, @ForAll("fraction1") f2: Fraction) {
        assertEquals(f1, f1 + f2 - f2, "f1: $f1    f2: $f2")

        assertEquals(f1, -(-f1), "f1: $f1 ")

        assertTrue((f1 - f1).isZero())

        if (f1 > f2) {
            assertTrue(f2 - f1 < 0)
        } else
        if (f1 < f2) {
            assertTrue(f2 - f1 > 0)
        }
    }

    @Property
    fun sumPropsInt(@ForAll("fraction1") f1: Fraction, @ForAll @IntRange(min = -100, max = +100) int: Int) {
        assertEquals(f1, f1 + int - int, "f1: $f1    int: $int")

        assertEquals(f1 + Fraction(int), f1 + int, "f1: $f1    int: $int")
        assertEquals(f1 - Fraction(int), f1 - int, "f1: $f1    int: $int")
    }

    @Property
    fun mulProps(@ForAll("fraction1") f1: Fraction, @ForAll("fraction1") f2: Fraction) {
        try {
            assertEquals(Fraction(1), f1 / f1, "f1: $f1")
        } catch (e: NumberFormatException) {
            println(e.message + "\nf1: $f1")
        }

        try {
            assertEquals(f1, f1 * f2 / f2, "f1: $f1    f2: $f2")
        } catch (e: NumberFormatException) {
            println(e.message + "\nf1: $f1    f2: $f2")
        }
    }

    @Property
    fun mulPropsInt(@ForAll("fraction1") f1: Fraction, @ForAll @IntRange(min = -100, max = +100) int: Int) {
        try {
            assertEquals(f1, f1 * int / int, "f1: $f1    int: $int")
        } catch (e: NumberFormatException) {
            println(e.message + "\nf1: $f1    int: $int")
        }

        assertEquals(f1 * Fraction(int), f1 * int, "f1: $f1    int: $int")

        try {
            assertEquals(f1 / Fraction(int), f1 / int, "f1: $f1    int: $int")
        } catch (e: NumberFormatException) {
            println(e.message + "\nf1: $f1    int: $int")
        }
    }

    @Property
    fun compareToInt(@ForAll("fraction1") f: Fraction, @ForAll i: Int) {
        if (f.num > f.den) {
            assertTrue(f > 1.absoluteValue * f.sign)
        }

        val frac = Fraction(i)
        assertEquals(f > i, f > frac, "$f > $i   $f > $frac")
    }

    @Property
    fun comparisonInversible(@ForAll("fraction1") f1: Fraction, @ForAll("fraction1") f2: Fraction) {
        assertEquals((f1 < f2), (f2 > f1), "$f1 < $f2")

    }

    @Property
    fun comparisonIsCorrect(@ForAll num: Int, @ForAll den: Int) {
        ///assertEquals()

    }
}