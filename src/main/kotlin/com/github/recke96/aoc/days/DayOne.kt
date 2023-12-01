package com.github.recke96.aoc.days

class DayOne() : AoCCommand("day-1") {
    override val firstDemo = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent()
    override val secondDemo: String
        get() = TODO("Not yet implemented")

    override fun solveFirstPart(input: String): String {
        return input.lineSequence()
            .map { line -> Pair(line.firstOrNull { it.isDigit() }, line.lastOrNull { it.isDigit() }) }
            .filter { it.first != null && it.second != null }
            .map { "${it.first}${it.second}" }
            .map { it.toInt() }
            .sum()
            .toString()
    }

    override fun solveSecondPart(input: String): String {
        TODO("Not yet implemented")
    }
}
