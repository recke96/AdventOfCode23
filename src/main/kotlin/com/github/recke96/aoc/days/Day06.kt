package com.github.recke96.aoc.days

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

class Day06 : AoCCommand("day-6") {

    override val firstDemo = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent()

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String {
        val races = parseRaces(input)

        return races.map { it.findWinningAccelerationTimes() }
            .map { it.last - it.first + 1 }
            .reduce(Long::times)
            .toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        val race = parseRace(input)

        val winningAccelerations = race.findWinningAccelerationTimes()
        val numberOfWinningAccelerations = winningAccelerations.last - winningAccelerations.first + 1

        return numberOfWinningAccelerations.toString()
    }
}

data class DistanceRace(val time: Long, val record: Long)

// Find both accelerations that allow us to reach the record distance
fun DistanceRace.findWinningAccelerationTimes(): LongRange {
    val time = time.toDouble()
    // fudge a little since we need to beat the record, not equal it
    val record = record.toDouble() + 1e-6

    val common = sqrt((time * time) - (4.0 * record))

    // Winning accelerations
    val lower = ceil((time - common) / 2.0).toLong()
    val upper = floor((time + common) / 2.0).toLong()

    return lower..upper
}

fun parseRaces(input: Sequence<String>): List<DistanceRace> {
    val numberRegex = Regex("""\d+""")
    val (timesInput, distancesInput) = input.toList();

    val times = numberRegex.findAll(timesInput).map { it.value.toLong() }
    val distances = numberRegex.findAll(distancesInput).map { it.value.toLong() }

    return times.zip(distances).map { DistanceRace(it.first, it.second) }.toList()
}

fun parseRace(input: Sequence<String>): DistanceRace {
    val numberRegex = Regex("""\d+""")
    val (timeInput, distanceInput) = input.toList();

    val time = numberRegex.findAll(timeInput)
        .map { it.value }
        .joinToString(separator = "")
        .toLong()

    val distance = numberRegex.findAll(distanceInput)
        .map { it.value }
        .joinToString(separator = "")
        .toLong()

    return DistanceRace(time, distance)
}
