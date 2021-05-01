package fraction

import net.jqwik.api.*
import net.jqwik.api.constraints.Positive
import net.jqwik.api.constraints.Size
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals

@PropertyDefaults(afterFailure = AfterFailureMode.SAMPLE_FIRST)
internal class IntListPBT {
    @Provide
    fun intList(): Arbitrary<IntList> {
        return Arbitraries.integers().greaterOrEqual(0).list().map { IntList(*it.toIntArray()) }
    }

    @Property
    fun copyIsIdentical(@ForAll("intList") l: IntList) {
        val copy = l.copy()

        assertArrayEquals(l.toArray(), copy.toArray())
    }

    @Property
    fun memberCountCountsMembersDuringConstruction(@ForAll @Size(max = 50) list: List<Int>) {
        val intList = IntList(*list.toIntArray())

        assertEquals(list.size, intList.memberCount)
    }

    @Property
    fun memberCountCountsMembers(@ForAll @Size(max = 100) list: List<Int>) {
        val intList = IntList()

        assertEquals(0, intList.memberCount)

        for (i in list) {
            intList.add(i)
        }

        assertEquals(list.size, intList.memberCount)

        intList.clear()

        assertEquals(0, intList.memberCount)

        for (i in list) {
            intList.addSorted(i)
        }

        assertEquals(list.size, intList.memberCount)
    }

    @Property
    fun alwaysSortedAddInt(@ForAll("intList") list1: IntList, @ForAll @Positive int: Int) {
        var list = list1.copy()
        list.addSorted(int)

        var original = list.toArray()
        var sorted = original.sortedArray()

        assertArrayEquals(sorted, original)
    }

    @Property
    fun alwaysSorted(@ForAll("intList") list1: IntList, @ForAll("intList") list2: IntList) {
        var original = list1.toArray()
        var sorted = original.sortedArray()

        assertArrayEquals(sorted, original)

        original = list2.toArray()
        sorted = original.sortedArray()

        assertArrayEquals(sorted, original)

        val list = list1.copy()
        list.addSorted(list2)
        original = list.toArray()
        sorted = original.sortedArray()

        assertEquals(list1.memberCount + list2.memberCount, list.memberCount)

        assertArrayEquals(sorted, original, "Adding $list2 to $list1, got $list")
    }

}