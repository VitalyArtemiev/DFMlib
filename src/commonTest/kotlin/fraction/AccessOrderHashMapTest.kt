package fraction

import kotlin.math.sign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AccessOrderHashMapTest {
    @Test
    fun test() {
        val m = AccessOrderHashMap(4)

        var a = arrayOf(1, 2, 3).toIntArray()

        m.put(1, a)

        assertEquals(a, m[1])

        a = arrayOf(2, 3, 4, 5).toIntArray()

        m.put(2, a)

        assertEquals(a, m[2])

        println(m.log)

        println(m.get(2))
        println(m.log)

        a = arrayOf(2, 3, 4, 5, 6).toIntArray()
        m.put(3, a)
        a = arrayOf(2, 3, 4, 5, 6, 7,).toIntArray()
        m.put(4, a)
        println(m.m)
        m.trim()
        println(m.m)
        a = arrayOf(2, 3, 4, 4, 6, 7,).toIntArray()
        m.put(5, a)
        println(m.m)

        m.trim()
        println(m.m)
    }
}
