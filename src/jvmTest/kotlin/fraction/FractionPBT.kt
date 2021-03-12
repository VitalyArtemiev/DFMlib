package fraction

import net.jqwik.api.*
import org.junit.jupiter.api.Assertions.assertEquals


//import org.junit.Assert.*
//import kotlin.test.*

@PropertyDefaults(afterFailure = AfterFailureMode.SAMPLE_FIRST)
internal class FractionPBT {
    @Provide
    fun fraction(): Arbitrary<Fraction> =
        Combinators.combine(Arbitraries.integers(), Arbitraries.integers().filter { i -> i != 0 })
            .`as` { a, b -> Fraction(a, b) }

    @Property
    fun sumProps(@ForAll("fraction") f1: Fraction, @ForAll("fraction") f2: Fraction) {
        assertEquals(f1, f1 + f2 - f2)
    }
}