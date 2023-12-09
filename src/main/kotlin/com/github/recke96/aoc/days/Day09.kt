package com.github.recke96.aoc.days

import java.util.*

class Day09 : AoCCommand("day-9") {

    override val firstDemo = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent()

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String {
        return input.map { it.split(" ").map(String::toInt) }
            .map { predict(it).second }
            .sum()
            .toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        return input.map { it.split(" ").map(String::toInt) }
            .map { predict(it).first }
            .sum()
            .toString()
    }

    private fun predict(dataRow: List<Int>): Pair<Int, Int> {
        val diffs: Deque<List<Int>> = LinkedList<List<Int>>().apply { push(dataRow) }
        while (diffs.first.any { it != 0 }) {
            val current = diffs.first.windowed(size = 2, step = 1)
                .map { it[1] - it[0] }
                .ifEmpty { listOf(0) }
            diffs.push(current)

        }

        var forwardPrediction = 0
        var backwardPrediction = 0
        while (diffs.isNotEmpty()) {
            val diff = diffs.pop()
            forwardPrediction += diff.last()
            backwardPrediction = diff.first() - backwardPrediction
        }

        return backwardPrediction to forwardPrediction
    }
}
