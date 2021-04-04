package fraction

class IntList(vararg values: Int) {
    init {
        for (value in values.sorted()) {
            add(value)
        }
    }

    var memberCount = 0
    internal var root: IntMember? = null

    private var prod: Long = 0
    private var prodReady = false

    inner class IntMember(var value: Int, var next: IntMember? = null)

    fun product(): Long {
        if (prodReady)
            return prod

        prodReady = true

        if (memberCount == 0) {
            prod = 0
            return prod
        }

        prod = 1
        var cur = root

        for (i in 1 until memberCount) {
            prod *= cur!!.value
            cur = cur.next
        }

        prod *= cur!!.value //to avoid nullptrexc

        return prod
    }

    fun copy(): IntList {
        val result = IntList()
        if (memberCount == 0)
            return result

        var cur = root!!.next
        result.root = IntMember(root!!.value)
        var resPrev: IntMember
        var resCur = result.root

        for (i in 1 until memberCount) {
            resPrev = resCur!!
            resCur = IntMember(cur!!.value)
            resPrev.next = resCur

            cur = cur.next
        }

        result.memberCount = memberCount
        return result
        //val arr = toArray().toIntArray()

        //return IntList(*arr)
    }

    internal fun add(v: Int) {
        if (root == null) {
            root = IntMember(v)
        } else {
            var cur = root
            while (cur!!.next != null) {
                cur = cur.next
            }
            cur.next = IntMember(v)
        }
        memberCount++
        prodReady = false
    }

    internal fun addSorted(v: Int) {
        if (root == null) {
            root = IntMember(v)
            memberCount++
        } else {
            memberCount++
            prodReady = false

            if (root!!.value >= v) {
                val t = IntMember(v)
                t.next = root
                root = t
                return
            }

            var cur = root
            while (cur!!.next != null && cur.next!!.value < v) {
                cur = cur.next
            }

            cur.next = IntMember(v, cur.next) //null handled
        }
    }

    internal fun addSorted(list: IntList) {
        if (list.memberCount == 0)
            return

        val l = list.copy()

        if (root == null) {
            root = l.root
        } else {
            var curL = l.root
            var prevL = curL
            var cur = root

            var flag = false
            while (curL != null && root!!.value >= curL.value) {
                flag = true
                prevL = curL
                curL = curL.next
            }

            if (flag) {
                prevL!!.next = root
                root = l.root
            }


            /*while (curL != null && root!!.value >= curL.value) { //add all elements smaller than current root to beginning
                val t = curL
                curL = curL.next
                t.next = root
                root = t
            }*/

            while (curL != null) {//add the rest interspersed in current list
                while (cur!!.next != null && cur.next!!.value < curL.value) {
                    cur = cur.next
                }

                cur.next = IntMember(curL.value, cur.next) //null handled
                curL = curL.next
            }
        }

        memberCount += l.memberCount
        prodReady = false
    }

    internal fun deleteFirst() {
        if (root == null)
            return
        prodReady = false

        root = root!!.next
        memberCount--
    }

    internal fun deleteNext(p: IntMember) {
        if (p.next == null)
            return
        prodReady = false

        p.next = p.next!!.next
        memberCount--
    }

    internal fun clear() {
        while (root != null) {
            val p = root
            root = root!!.next
            p!!.next = null
        }
        memberCount = 0

        prod = 0
        prodReady = true
    }

    override fun toString(): String {
        if (memberCount == 0)
            return "0"

        var cur: IntMember? = root
        var result = cur!!.value.toString()
        cur = cur.next

        while (cur != null) {
            result += "*" + cur.value.toString()
            cur = cur.next
        }

        return result
    }

    fun toArray(): Array<Int> {
        var cur: IntMember? = root ?: return emptyArray()
        return Array(memberCount) {
            val v = cur
            cur = cur?.next
            v!!.value
        }
    }
}
