package com.github.recke96.aoc.days

class DayOne() : AoCCommand("day-1") {

    override val firstDemo = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent()

    override val secondDemo = """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent()


    override fun solveFirstPart(input: Sequence<String>): String = input
        .map { line -> line.firstOrNull { it.isDigit() } to line.lastOrNull { it.isDigit() } }
        .filter { it.first != null && it.second != null }
        .map { "${it.first}${it.second}" }
        .map { it.toInt() }
        .sum()
        .toString()


    private val replacement = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )

    private val interestingStrings = replacement.keys.union(replacement.values).toSet()

    override fun solveSecondPart(input: Sequence<String>): String = input
        .map { it.findAnyOf(interestingStrings)?.second to it.findLastAnyOf(interestingStrings)?.second }
        .map { "${replace(it.first)}${replace(it.second)}" }
        .map { it.toInt() }
        .sum()
        .toString()

    private fun replace(digitOrSpelled: String?): String {
        checkNotNull(digitOrSpelled)
        if (digitOrSpelled.length > 1) {
            return replacement[digitOrSpelled] ?: throw IllegalStateException()
        }
        return digitOrSpelled
    }
}
