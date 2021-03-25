package fraction

import kotlin.test.Test
import kotlin.test.assertEquals

internal class IntListTest {
    @Test
    fun stest() {
        val l1 = IntList(1)
        val l2 = IntList(0)

        l2.addSorted(l1)

        println("$l2")
    }

    @Test
    fun test() {
        val l = IntList()
        l.add(1)
        assertEquals(1, l.product().toLong(), "Product failed")

        l.add(3)
        assertEquals(3, l.product().toLong(), "Product failed")

        assertEquals(2, l.memberCount.toLong(), "Wrong number of elements")
        assertEquals("1*3", l.toString())

        l.addSorted(2)
        assertEquals("1*2*3", l.toString(), "Addsorted failed")
        l.deleteFirst()
        assertEquals("2*3", l.toString(), "Deletefirst failed")
        assertEquals(6, l.product().toLong(), "Product failed")

        l.addSorted(4)
        l.addSorted(1)
        assertEquals("1*2*3*4", l.toString(), "Sort first element failed")
        val t = l.copy()
        assertEquals(l.toString(), t.toString(), "Copy failed")
        assertEquals(l.memberCount.toLong(), t.memberCount.toLong(), "Copy failed")

        l.clear()
        t.clear()

        l.add(3)
        l.add(4)

        t.add(2)
        t.add(2)

        l.addSorted(t)

        assertEquals(4, l.memberCount.toLong(), "Addsorted failed")
        assertEquals("2*2*3*4", l.toString(), "Addsorted failed")
        l.deleteFirst()
        assertEquals((2 * 3 * 4).toLong(), l.product().toLong(), "Product failed")

        l.clear()
        t.clear()

        l.add(2)
        l.add(4)
        l.add(6)
        l.add(8)

        t.add(1)
        t.add(3)
        t.add(5)
        t.add(7)
        t.add(9)
        t.add(9)

        var k = l.copy()

        l.addSorted(t)

        assertEquals(10, l.memberCount.toLong(), "Addsorted failed")
        assertEquals("1*2*3*4*5*6*7*8*9*9", l.toString(), "Addsorted failed")
        assertEquals((2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 9).toLong(), l.product().toLong(), "product failed")

        t.addSorted(k)
        assertEquals("1*2*3*4*5*6*7*8*9*9", t.toString(), "Addsorted failed")

        l.clear()
        t.clear()

        t.add(2)
        t.add(2)
        t.add(5)

        l.add(1)
        l.add(4)
        l.add(6)

        l.addSorted(t)
        assertEquals("1*2*2*4*5*6", l.toString(), "Addsorted failed")

        l.clear()
        t.clear()

        l.add(2)
        l.add(2)
        l.add(5)

        t.add(1)
        t.add(6)

        l.addSorted(t)
        assertEquals("1*2*2*5*6", l.toString(), "Addsorted failed")
    }
}