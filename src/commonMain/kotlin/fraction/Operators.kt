package fraction

import matrix.Matrix
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sign

operator fun Number.unaryMinus(): Number {
    if (this is Fraction)
        return -this

    if (this is Double)
        return -this

    throw Exception("Not implemented")
}

operator fun Number.minus(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this - a

    if (this is Double && a is Double)
        return this - a

    throw Exception("Not implemented")
}

operator fun Number.minus(a: Matrix): Matrix {
    return  - a + this
}

operator fun Number.plus(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this + a

    if (this is Double && a is Double)
        return this + a

    throw Exception("Not implemented")
}

operator fun Number.plus(a: Matrix): Matrix {
    return  a + this
}

operator fun Number.times(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this * a

    if (this is Double && a is Double)
        return this * a

    if (this is Fraction && a is Int)
        return this * a

    if (this is Double && a is Int)
        return this * a

    throw Exception("Not implemented")
}

operator fun Number.times(a: Matrix): Matrix {
    return a * this
}

operator fun Number.div(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this / a

    if (this is Double && a is Double)
        return this / a

    throw Exception("Not implemented")
}

fun Number.isZero(): Boolean {
    if (this is Fraction)
        return isZero

    if (this is Double)
        return this == 0.0

    throw Exception("Not implemented")
}

operator fun Number.compareTo(b: Number): Int {
    val result = this - b
    return when (result) {
        is Fraction -> {
            if (result.num == 0) {
                0
            } else {
                result.sign
            }
        }
        is Double -> {
            result.sign.toInt()
        }
        else -> {
            throw Exception("Not implemented")
        }
    }
}

fun String.toFraction(): Fraction {
    return Fraction.valueOf(this)
}

fun String.toMatrix(): Matrix {
    return Matrix(this)
}

fun Number.roundP(p: Int = 100000): Number {
    if (this is Fraction)
        return this

    if (this is Double)
        return round(this * p) / p

    throw Exception("Not implemented")
}

fun roundP(x: Double, p: Int = 100000) = round(x * p) / p

fun abs(a: Number): Number {
    return when (a) {
        is Double -> {
            abs(a)
        }
        is Fraction -> {
            if (a.sign == -1) {
                a.multiply(-1)
            }
            a
        }
        else -> throw Exception("Not implemented")
    }
}