package com.github.recke96.aoc.days

class Day03 : AoCCommand("day-3") {
    override val firstDemo = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...${'$'}.*....
        .664.598..
    """.trimIndent()

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String {
        val schematicParts = parseSchematicParts(input)

        return schematicParts.values.asSequence()
            .flatten()
            .filterIsInstance<SerialNumber>() // we only care for neighbors of SerialNumbers
            .filter { schematicParts.hasEnginePartNeighbor(it) }
            .sumOf { it.number }
            .toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        val schematicParts = parseSchematicParts(input)
        return schematicParts.values.asSequence()
            .flatten()
            .filterIsInstance<EnginePart>()
            .filter { it.symbol == '*' } // Gears have symbol '*'
            .map { schematicParts.serialNumbers(it) }
            .filter { it.size == 2 } // Gears have exactly 2 serial numbers
            .map { it.fold(1) { acc, serial -> acc * serial.number } } // calculate gear ratio
            .sum()
            .toString()
    }

    private fun parseSchematicParts(input: Sequence<String>): Map<Int, List<SchematicPart>> {
        val partsAndNumbers = Regex("""(?<number>\d+)|(?<part>[^.\d\sA-Za-z])""")
        return input.flatMapIndexed { lineNum, line ->
            partsAndNumbers.findAll(line).map { match ->
                match.groups["number"]?.let { SerialNumber(it.value.toInt(), lineNum, it.range) }
                    ?: match.groups["part"]?.let { EnginePart(it.value.single(), lineNum, it.range.single()) }
                    ?: throw IllegalStateException("Shouldn't happen, we know that either of the groups must match")
            }
        }.fold(mutableMapOf<Int, MutableList<SchematicPart>>()) { acc, part ->
            acc.apply {
                compute(part.row) { _, value ->
                    value?.apply { add(part) } ?: mutableListOf(part)
                }
            }
        }
    }
}

fun Map<Int, List<SchematicPart>>.serialNumbers(part: EnginePart): Set<SerialNumber> {
    val neighborRows = (part.row - 1)..(part.row + 1)
    val neighborCols = (part.cols.first - 1)..(part.cols.last + 1)
    return neighborRows.cartesianProduct(neighborCols) // generate all coordinates of the part & its neighbors
        .filter { !(it.first == part.row && it.second in part.cols) }
        .mapNotNull { schematicPartNeighborAt<SerialNumber>(part, it.first, it.second) }
        .toSet()
}

fun Map<Int, List<SchematicPart>>.hasEnginePartNeighbor(part: SerialNumber): Boolean {
    val neighborRows = (part.row - 1)..(part.row + 1)
    val neighborCols = (part.cols.first - 1)..(part.cols.last + 1)
    return neighborRows.cartesianProduct(neighborCols) // generate all coordinates of the part & its neighbors
        .filter { !(it.first == part.row && it.second in part.cols) } // filter own coordinates
        .any { schematicPartNeighborAt<EnginePart>(part, it.first, it.second) != null }
}

inline fun <reified T : SchematicPart> Map<Int, List<SchematicPart>>.schematicPartNeighborAt(
    part: SchematicPart, row: Int, col: Int
): T? {
    val partAt = get(row)?.singleOrNull { col in it.cols }
    return when (partAt) {
        part -> null // Is itself, not a neighbor
        is T -> partAt // Is neighbor of correct type
        else -> null
    }
}

fun IntRange.cartesianProduct(other: IntRange): Set<Pair<Int, Int>> = flatMap { outer ->
    other.map { inner -> outer to inner }
}.toSet()

sealed interface SchematicPart {
    val row: Int
    val cols: IntRange
}

data class SerialNumber(val number: Int, override val row: Int, override val cols: IntRange) : SchematicPart
data class EnginePart(val symbol: Char, override val row: Int, val col: Int) : SchematicPart {
    override val cols = col..col
}
