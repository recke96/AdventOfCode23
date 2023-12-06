package com.github.recke96.aoc.days

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

class Day06 : AoCCommand("day-6") {

    override val firstDemo = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent()

    override val secondDemo: String
        get() = TODO("Not yet implemented")

    override fun solveFirstPart(input: Sequence<String>): String {
        val races = parseRaces(input)

        return races.map { it.findWinningAccelerationTimes() }
            .map { it.last - it.first + 1 }
            .reduce(Int::times)
            .toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        TODO("Not yet implemented")
    }


}


data class DistanceRace(val time: Int, val record: Int)


// Find both accelerations that allow us to reach the record distance
fun DistanceRace.findWinningAccelerationTimes(): IntRange {
    val time = time.toDouble()
    // fudge a little since we need to beat the record, not equal it
    val record = record.toDouble() + 1e-6

    val common = sqrt((time * time) - (4.0 * record))

    // Winning accelerations
    val lower = ceil((time - common) / 2.0).toInt().let { if (it == this.record) it + 1 else it }
    val upper = floor((time + common) / 2.0).toInt().let { if (it == this.record) it - 1 else it }

    return lower..upper
}

fun parseRaces(input: Sequence<String>): List<DistanceRace> {
    val numberRegex = Regex("""\d+""")
    val (timesInput, distancesInput) = input.toList();

    val times = numberRegex.findAll(timesInput).map { it.value.toInt() }
    val distances = numberRegex.findAll(distancesInput).map { it.value.toInt() }

    return times.zip(distances).map { DistanceRace(it.first, it.second) }.toList()
}
