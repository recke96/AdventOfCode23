package com.github.recke96.aoc.days

import kotlin.math.abs

class Day11 : AoCCommand("day-11") {

    override val firstDemo = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent()
    override val secondDemo: String
        get() = TODO("Not yet implemented")

    override fun solveFirstPart(input: Sequence<String>): String {
        val galaxies =
            input.flatMap(::duplicateEmpty).transpose()
                .flatMap(::duplicateEmpty).transpose()
                .flatMapIndexed { row, line ->
                    line.mapIndexedNotNull { col, char ->
                        if (char == '#') row to col else null
                    }
                }.toList()

        return galaxies.flatMapIndexed { i: Int, galaxy: Pair<Int, Int> ->
            galaxies.drop(i + 1).map { other -> galaxy to other }
        }.sumOf { steps(it.first, it.second) }.toString()
    }

    private fun steps(a: Pair<Int, Int>, b: Pair<Int, Int>): Int = abs(a.first - b.first) + abs(a.second - b.second)

    private fun Sequence<String>.transpose(): Sequence<String> = sequence {
        val allLines = this@transpose.toList()
        for (i in allLines.first().indices) {
            yield(allLines.map { it[i] }.joinToString(""))
        }
    }

    private fun duplicateEmpty(stretch: String): List<String> =
        if (stretch.all { it == '.' }) listOf(stretch, stretch) else listOf(stretch)

    override fun solveSecondPart(input: Sequence<String>): String {
        TODO("Not yet implemented")
    }
}
