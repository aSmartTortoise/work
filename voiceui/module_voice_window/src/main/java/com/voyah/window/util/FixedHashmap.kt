package com.voyah.window.util

object RequestType {
    const val REQUEST_TYPE_NORMAL = 0
    const val REQUEST_TYPE_LANTU = 1
    const val REQUEST_TYPE_DEEPSEEK = 2
    private var fixedMap = FixedHashmap<String, Int>(5)
    init {

    }

    fun put(key: String, value: Int = 0) {
        if (fixedMap.containsKey(key)) {
            if (fixedMap[key] == REQUEST_TYPE_DEEPSEEK) {
                return
            } else if (fixedMap[key] == REQUEST_TYPE_LANTU) {
                if (value > REQUEST_TYPE_LANTU) {
                    fixedMap[key] = value
                }
            }
        } else {
            fixedMap[key] = value
        }
    }

    fun get(key: String): Int {
        return fixedMap[key] ?: REQUEST_TYPE_NORMAL
    }
}

class FixedHashmap<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>() {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maxSize
    }
}