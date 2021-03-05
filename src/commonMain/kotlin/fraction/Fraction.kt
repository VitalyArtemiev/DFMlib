/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fraction

import kotlin.math.abs
import kotlin.math.sign

/**
 * @author Виталий
 */
class Fraction: Number {
    override fun toChar(): Char {
        return 'f'
    }

    override fun toDouble(): Double {
        return (num.toDouble() / den.toDouble())
    }

    override fun toFloat(): Float {
        return (num.toFloat() / den.toFloat())
    }

    override fun toInt(): Int {
        return num / den
    }

    override fun toLong(): Long {
        return (num / den).toLong()
    }

    override fun toShort(): Short {
        return (num / den).toShort()
    }

    override fun toByte(): Byte {
        return (num / den).toByte()
    }

    var numerator: IntList
    var denominator: IntList
    var num: Int = 0
    var den: Int = 0

    var sign = 1

    val isZero: Boolean
        get() = num == 0

    constructor() {
        num = 0
        den = 1

        numerator = IntList(0)
        denominator = IntList(1)
    }

    //@Throws(NumberFormatException::class)
    constructor(num: Int, den: Int = 1) {
        this.num = num
        this.den = den

        //require(den != 0) {}
        if (this.den == 0) {
            throw NumberFormatException("denominator cannot be null")
        }

        if (this.den < 0) {
            this.num *= -1
            this.den = abs(this.den)
        }

        numerator = factorize(this.num)
        denominator = factorize(this.den)
        simplify()
    }

    private inline fun handleZero(): Boolean {
        return if (num == 0) {
            den = 1
            numerator.clear()
            numerator.add(0)
            denominator.clear()
            denominator.add(1)
            true
        } else
            false
    }

    private fun factorize(number: Int): IntList {
        sign *= number.sign
        if (sign == 0)
            sign = 1

        var n = abs(number)

        val factors = IntList()
        var i = 2
        while (i <= n / i) {
            while (n % i == 0) {
                factors.add(i)
                n /= i
            }
            i++
        }

        /*for (i in 2..(n / i)) {
            while (n % i == 0) {
                factors.add(i)
                n /= i
            }
        }*/
        if (n > 1) {
            factors.add(n)
        }
        return factors
    }

    fun copy(): Fraction {
        val result = Fraction()
        result.num = num
        result.den = den
        result.sign = sign
        result.numerator = numerator.copy()
        result.denominator = denominator.copy()
        return result
    }

    override fun equals(other: Any?): Boolean { //cannot make it an operator. possible bug
        return when (other) {
            is Fraction -> {
                num == other.num && den == other.den
            }
            is Int -> {
                num == other && den == 1
            }
            else -> {
                false
            }
        }
    }

    fun negate() {
        num *= -1
        sign *= -1
        //numerator.addSorted(-1);
        //simplify(); todo: possible error
    }

    operator fun unaryMinus(): Fraction {
        val result = copy()
        //System.out.println("un0 " + result.toString())
        with(result) {
            negate()
        }
        //System.out.println("un1 " + result.toString())
        return result
    }

    fun add(f: Fraction) {
        num = num * f.den + den * f.num

        if (handleZero()) {
            return
        }

        den *= f.den
        numerator.clear()
        numerator = factorize(num)
        denominator.addSorted(f.denominator)
        simplify()
    }

    fun subtract(f: Fraction) {
        num = num * f.den - den * f.num

        if (handleZero()) {
            return
        }

        den *= f.den
        numerator.clear()
        numerator = factorize(num)
        denominator.addSorted(f.denominator)
        simplify()
    }

    fun multiply(f: Fraction) {
        num *= f.num

        if (handleZero()) {
            return
        }

        den *= f.den

        numerator.addSorted(f.numerator)
        denominator.addSorted(f.denominator)

        simplify()
    }

    fun divide(f: Fraction) {
        num *= f.den
        den *= f.num

        if (den == 0) {
            throw NumberFormatException("Division by zero")
        }

        numerator.addSorted(f.denominator)
        denominator.addSorted(f.numerator)

        simplify()
    }

    fun add(a: Int) {
        num += a * den
        numerator.clear()
        numerator = factorize(num)
        simplify()
    }

    fun subtract(a: Int) {
        num -= a * den
        numerator.clear()
        numerator = factorize(num)
        simplify()
    }

    fun multiply(a: Int) {
        num *= a

        if (num == 0) {
            den = 1
            numerator.clear()
            numerator.add(0)
            denominator.clear()
            denominator.add(1)
            sign = 1
            return
        }

        numerator.addSorted(factorize(a))
        simplify()
    }

    fun divide(a: Int) {
        den *= a

        if (den == 0) {
            throw NumberFormatException("Division by zero")
        }

        denominator.addSorted(factorize(a))
        simplify()
    }

    operator fun plus(f: Number): Fraction {
        val result = copy()
        with (result) {
            when (f) {
                is Fraction -> {
                    add(f)
                }
                is Int -> {
                    add(f)
                }
                else -> {
                    throw Exception("Wrong parameter type")
                }
            }
        }
        return result
    }

    operator fun minus(f: Number): Fraction {
        val result = copy()
        with (result) {
            when (f) {
                is Fraction -> {
                    subtract(f)
                }
                is Int -> {
                    subtract(f)
                }
                else -> {
                    throw Exception("Wrong parameter type")
                }
            }
        }
        return result
    }

    operator fun times(f: Number): Fraction {
        val result = copy()
        with (result) {
            when (f) {
                is Fraction -> {
                    multiply(f)
                }
                is Int -> {
                    multiply(f)
                }
                else -> {
                    throw Exception("Wrong parameter type")
                }
            }
        }
        return result
    }

    operator fun div(f: Number): Fraction {
        val result = copy()
        with (result) {
            when (f) {
                is Fraction -> {
                    divide(f)
                }
                is Int -> {
                    divide(f)
                }
                else -> {
                    throw Exception("Wrong parameter type")
                }
            }
        }
        return result
    }

    fun simplify() {
        if (den < 0) {
            sign *= -1
            num *= -1
            den = abs(den)
        }

        if (num * sign < 0) {
            //println("Warning: sign mismatch!!!")//todo: triggers in tests. problem?
            sign *= -1
        }

        if (num == 1 || den == 1) {
            return
        }

        if (num == den) {
            num = 1
            den = 1
            numerator.clear()
            numerator.add(1)
            denominator.clear()
            denominator.add(1)
            return
        }

        var cn = numerator.root   //current
        var cd = denominator.root
        var pn: IntList.IntMember? = null             //previous
        var pd: IntList.IntMember? = null

        while (cn != null) {
            while (cd != null && cd.value < cn.value) {
                if (cd.value < 0) {
                    //sign *= -1; no need
                    cd.value = abs(cd.value)
                }

                pd = cd
                cd = cd.next
            }

            if (cd == null) {
                break
            }

            if (cn.value < 0) {
                //sign *= -1; no need
                cn.value = abs(cn.value)
            }

            if (cn.value == cd.value) {
                if (pn != null) {
                    numerator.deleteNext(pn)
                    cn = pn.next
                } else {
                    numerator.deleteFirst()
                    cn = numerator.root
                }

                if (pd != null) {
                    denominator.deleteNext(pd)
                    cd = pd.next
                } else {
                    denominator.deleteFirst()
                    cd = denominator.root
                }
            } else {
                pn = cn
                cn = cn.next
            }
        }

        if (numerator.memberCount == 0) {
            numerator.add(1)
        }
        if (denominator.memberCount == 0) {
            denominator.add(1)
        }

        num = sign * numerator.product()
        den = denominator.product()
    }

    fun toStringTruncated(): String {
        if (num == 0) {
            return "0"
        }
        if (den == 1) {
            return num.toString()
        }
        return toString()
    }

    override fun toString(): String {
        return "$num/$den"
    }

    fun toStringFactorized(): String {
        return (if (sign < 0) "-" else "") + numerator.toString() + "/" + denominator.toString()
    }

    companion object {
        fun valueOf(s: String): Fraction {
            try {
                return if (s.contains("/")) {
                    val elements = s.split("/").toTypedArray()
                    if (elements.size > 2)
                        throw NumberFormatException("Malformed fraction")

                    val n: Int
                    val d: Int
                    n = elements[0].toInt()
                    d = elements[1].toInt()
                    Fraction(n, d)
                } else {
                    Fraction(s.toInt())
                }
            } catch (e: NumberFormatException) {
                throw NumberFormatException("Malformed fraction")
            }
        }
    }
}
