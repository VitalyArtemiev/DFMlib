package matrix

import fraction.Fraction

abstract class Array2D/*<T> where T: Number*/ {
    abstract operator fun get(i: Int, j: Int): Number

    abstract operator fun get(i: Int): Any

    abstract operator fun set(i: Int, j: Int, v: Number)

    abstract operator fun set(i: Int, a: Any)

    abstract fun copy(): Array2D
}

class Array2DDouble(r: Int, c: Int) : Array2D() {
    var a: Array<DoubleArray> = Array(r) { DoubleArray(c) }
    //todo: range check?
    override fun get(i: Int, j: Int): Double {
        return a[i][j]
    }

    override fun get(i: Int): DoubleArray {
        return a[i]
    }

    override fun set(i: Int, j: Int, v: Number) {
        a[i][j] = v as Double
    }

    override fun set(i: Int, a: Any) {
        this.a[i] = a as DoubleArray
    }

    override fun copy(): Array2D {
        val result = Array2DDouble(a.size, a[0].size)
        a.forEachIndexed {i, el -> result[i] = el.copyOf() }
        return result
    }
}

class Array2DFraction(r: Int, c: Int) : Array2D() {
    var a: Array<Array<Fraction>> = Array(r) { Array(c) { Fraction() } }

    override fun get(i: Int, j: Int): Fraction {
        return a[i][j]
    }

    override fun get(i: Int): Array<Fraction> {
        return a[i]
    }

    override fun set(i: Int, j: Int, v: Number) {
        a[i][j] = v as Fraction
    }

    override fun set(i: Int, a: Any) {
        this.a[i] = a as Array<Fraction>
    }

    override fun copy(): Array2D {
        val result = Array2DFraction(a.size, a[0].size)
        a.forEachIndexed {i, el -> result[i] = el.copyOf() }
        return result
    }
}
//https://stackoverflow.com/questions/41941102/instantiating-generic-array-in-kotlin