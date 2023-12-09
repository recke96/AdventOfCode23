package com.github.recke96.aoc.days

import java.util.*

class Day09 : AoCCommand("day-9") {

    override val firstDemo = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent()
    override val secondDemo: String
        get() = TODO("Not yet implemented")

    override fun solveFirstPart(input: Sequence<String>): String {
        return input.map { it.split(" ").map { it.toInt() } }
            .map { predict(it) }
            .sum()
            .toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        TODO("Not yet implemented")
    }

    private fun predict(dataRow: List<Int>): Int {
        val diffs: Deque<List<Int>> = LinkedList<List<Int>>().apply { push(dataRow) }
        while (diffs.first.any { it != 0 }) {
            val current = diffs.first.windowed(size = 2, step = 1)
                .map { it[1] - it[0] }
                .ifEmpty { listOf(0) }
            diffs.push(current)

        }

        var prediction = diffs.pop().last()
        while (diffs.isNotEmpty()) {
            val diff = diffs.pop()
            prediction += diff.last()
        }

        return prediction
    }
}
