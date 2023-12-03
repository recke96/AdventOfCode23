package com.github.recke96.aoc.days

class DayThree : AoCCommand("day-3") {
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

    override val secondDemo: String
        get() = TODO("Not yet implemented")


    override fun solveFirstPart(input: Sequence<String>): String {
        val partsAndNumbers = Regex("""(?<number>\d+)|(?<part>[^.\d\sA-Za-z])""")
        val schematicParts = input.flatMapIndexed { lineNum, line ->
            partsAndNumbers.findAll(line)
                .map { match ->
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

        return schematicParts.values.asSequence().flatten()
            .filterIsInstance<SerialNumber>() // we only care for neighbors of SerialNumbers
            .filter { schematicParts.hasEnginePartNeighbor(it) }
            .sumOf { it.number }
            .toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        TODO("Not yet implemented")
    }
}

fun Map<Int, List<SchematicPart>>.hasEnginePartNeighbor(part: SerialNumber): Boolean {
    val neighborRows = (part.row - 1)..(part.row + 1)
    val neighborCols = (part.cols.first - 1)..(part.cols.last + 1)
    return neighborRows.cartesianProduct(neighborCols) // generate all coordinates of the part & its neighbors
        .filter { !(it.first == part.row && it.second in part.cols) } // filter own coordinates
        .any { hasEnginePartNeighborAt(part, it.first, it.second) }
}

fun Map<Int, List<SchematicPart>>.hasEnginePartNeighborAt(part: SerialNumber, row: Int, col: Int): Boolean {
    val partAt = get(row)?.singleOrNull { col in it.cols }
    return partAt != null && partAt != part && partAt is EnginePart
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
