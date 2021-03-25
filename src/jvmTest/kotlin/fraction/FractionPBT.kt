package fraction

import net.jqwik.api.*
import org.junit.jupiter.api.Assertions.assertEquals


//import org.junit.Assert.*
//import kotlin.test.*

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
}