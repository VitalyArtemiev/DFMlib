package fraction

import net.jqwik.api.*
import net.jqwik.api.constraints.IntRange
import org.junit.jupiter.api.Assertions.assertEquals

@PropertyDefaults(afterFailure = AfterFailureMode.SAMPLE_FIRST)
internal class FractionPBT {
    @Provide
    fun fraction1(): Arbitrary<Fraction> =
        Combinators.combine(Arbitraries.integers().between(-100, 100),
            Arbitraries.integers().between(-100, 100).filter { i -> i != 0 })
            .`as` { a, b -> Fraction(a, b) }

    @Property
    fun sumProps(@ForAll("fraction1") f1: Fraction, @ForAll("fraction1") f2: Fraction) {
        assertEquals(f1, f1 + f2 - f2, "f1: $f1    f2: $f2")

        assertEquals(f1, -(-f1), "f1: $f1 ")
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
}