package matrix

import fraction.Fraction

class DoubleMatrix(r: Int, c: Int, init: (Int, Int) -> Double) :
    AltMatrix<Double>(r, c, Array(r) { i -> Array(c) { j -> init(i, j) } })  {
    constructor (r: Int, c: Int): this(r, c, {_,_ -> 0.0})
}

class FractionMatrix(r: Int, c: Int, init: (Int, Int) -> Fraction) :
    AltMatrix<Fraction>(r, c, Array(r) { i -> Array(c) { j -> init(i, j) } })  {
    constructor (r: Int, c: Int): this(r, c, {_,_ -> Fraction() })
}

open class AltMatrix<T: Number>  (r: Int, c: Int, val a: Array<Array<T>>) {
    operator fun plus(other: AltMatrix<T>) {
        //this[1,1] = other[1,1] + this[1,1]
    }

    operator fun get(i: Int, j: Int): T {
        return a[i][j]
    }

    operator fun get(i: Int): Array<T> {
        return a[i]
    }

    operator fun set(i: Int, j: Int, v: T) {
        a[i][j] = v
    }

    operator fun set(i: Int, a: Array<T>) {
        this.a[i] = a
    }

    fun copy(): AltMatrix<T> {
        val result = AltMatrix(a.size, a[0].size, a.copyOf()) //todo: potentially not deep copy
        //a.forEachIndexed {i, el -> result[i] = el.copyOf() }
        return result
    }
}
