package com.ma.tehro.common

class PriorityQueue<T>(private val comparator: Comparator<T>) {
    private val elements = mutableListOf<T>()

    fun add(element: T) {
        elements.add(element)
        siftUp(elements.lastIndex)
    }

    fun poll(): T? {
        if (elements.isEmpty()) return null
        if (elements.size == 1) return elements.removeAt(0)

        val root = elements[0]
        elements[0] = elements.removeAt(elements.lastIndex)
        siftDown(0)
        return root
    }

    fun isNotEmpty(): Boolean = elements.isNotEmpty()

    private fun siftUp(index: Int) {
        var child = index
        while (child > 0) {
            val parent = (child - 1) / 2
            if (comparator.compare(elements[child], elements[parent]) >= 0) break
            elements.swap(child, parent)
            child = parent
        }
    }

    private fun siftDown(index: Int) {
        var parent = index
        while (true) {
            var child = 2 * parent + 1
            if (child >= elements.size) break
            if (child + 1 < elements.size && comparator.compare(elements[child + 1], elements[child]) < 0) {
                child++
            }
            if (comparator.compare(elements[parent], elements[child]) <= 0) break
            elements.swap(parent, child)
            parent = child
        }
    }

    private fun MutableList<T>.swap(i: Int, j: Int) {
        val tmp = this[i]
        this[i] = this[j]
        this[j] = tmp
    }
}