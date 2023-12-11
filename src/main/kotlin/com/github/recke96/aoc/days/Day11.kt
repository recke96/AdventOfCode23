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

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String = distances(input.toList(), 2)

    override fun solveSecondPart(input: Sequence<String>): String = distances(input.toList(), 1_000_000)

    private fun distances(inputList: List<String>, expansionCompensation: Long): String {
        val emptyRows = inputList.mapIndexedNotNull { row, line -> if (line.all { it == '.' }) row.toLong() else null }
        val emptyCols =
            inputList.first().indices.mapNotNull { col -> if (inputList.all { it[col] == '.' }) col.toLong() else null }

        val galaxies = inputList.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, char ->
                if (char == '#') row.toLong() to col.toLong() else null
            }
        }

        return galaxies.flatMapIndexed { i: Int, galaxy: Pair<Long, Long> ->
            galaxies.drop(i + 1).map { other -> galaxy to other }
        }.sumOf {
            val rowSteps = stretch(it.first.first, it.second.first).steps(emptyRows, expansionCompensation)
            val colSteps = stretch(it.first.second, it.second.second).steps(emptyCols, expansionCompensation)

            rowSteps + colSteps
        }.toString()
    }

    private fun stretch(a: Long, b: Long): LongRange = if (a <= b) a..b else b..a

    private fun LongRange.steps(emptyStretches: List<Long>, expansionCompensation: Long): Long {
        val steps = abs(first - last)
        val emptyExpansionCompensation = (expansionCompensation - 1) * emptyStretches.count { it in this }

        return steps + emptyExpansionCompensation
    }
}
