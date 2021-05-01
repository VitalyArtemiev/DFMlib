package fraction

class AccessOrderHashMap (var targetSize:Int = 128,
                          val m: HashMap<Long, IntArray> = HashMap(targetSize)): MutableMap<Long, IntArray> by m {
    val log = hashMapOf<Long, Int>()

    fun trim() {
        var sorted = log.asSequence().sortedBy { it.value }
        sorted = sorted.take(sorted.count() - targetSize)

        for ((key, _) in sorted) {
            m.remove(key)
        }
    }

    override fun get(key: Long): IntArray? {
        if (log[key] != null) {
            log.put(key, log[key]!! + 1)
        } else {
            log[key] = 1
        }
        return m[key]
    }

    override fun put(key: Long, value: IntArray): IntArray? {
        if (log[key] != null) {
            log.put(key, log[key]!! + 1)
        } else {
            log[key] = 1
        }
        return m.put(key, value)
    }

    override fun toString(): String {
        var s = ""
        for ((key, value) in m) {
            s+= "$key: ${value.toList()} - ${log[key]}\n"
        }
        return s
    }
}