package com.github.recke96.aoc.days

import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken
import java.util.NoSuchElementException
import java.util.TreeMap

class Day05 : AoCCommand("day-5") {
    override val firstDemo = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent()

    override val secondDemo: String
        get() = TODO("Not yet implemented")

    override fun solveFirstPart(input: Sequence<String>): String {
        val almanac = AlmanacGrammar.parseOrThrow(input.joinToString(separator = "\n"))

        var currentCategory = Category.Seed
        var currentValues = almanac.seeds
        while (currentCategory != Category.Location) {
            val map = almanac.maps.find { it.source == currentCategory }
                ?: throw NoSuchElementException("No suitable map for source category $currentCategory")

            currentValues = currentValues.map(map::get)
            currentCategory = map.destination
        }

        return currentValues.min().toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        TODO("Not yet implemented")
    }
}

enum class Category { Seed, Soil, Fertilizer, Water, Light, Temperature, Humidity, Location }

data class MapRange(val sourceStart: Long, val destinationStart: Long, val length: Long)

data class RangeMapToken(val value: Long, val length: Long)

@JvmInline
value class RangeMap(private val map: TreeMap<Long, RangeMapToken>) {
    operator fun get(key: Long): Long? = map.floorEntry(key)?.let {
        val offset = key - it.key
        if (offset < it.value.length) {
            // Entry is in range
            it.value.value + offset
        } else {
            null
        }
    }
}


fun Iterable<MapRange>.asRangeMap(): RangeMap = RangeMap(associateTo(TreeMap<Long, RangeMapToken>()) {
    it.sourceStart to RangeMapToken(
        it.destinationStart,
        it.length
    )
})

data class AlmanacMap(val source: Category, val destination: Category, private val map: RangeMap) {

    constructor(source: Category, destination: Category, maps: List<MapRange>) : this(
        source,
        destination,
        maps.asRangeMap()
    )

    operator fun get(source: Long): Long = map[source] ?: source
}

data class Almanac(val seeds: List<Long>, val maps: List<AlmanacMap>)

data object AlmanacGrammar : Grammar<Almanac>() {

    init {
        // Skip whitespace
        regexToken("""\s+""", ignored = true)
    }

    private val long: Parser<Long> by regexToken("""\d+""") map { it.text.toLong() }

    private val seedPrefix by literalToken("seeds:")
    private val mapSeparator by literalToken("-to-")
    private val mapSuffix by literalToken("map:")

    private val categorySeed by literalToken("seed") map { Category.Seed }
    private val categorySoil by literalToken("soil") map { Category.Soil }
    private val categoryFertilizer by literalToken("fertilizer") map { Category.Fertilizer }
    private val categoryWater by literalToken("water") map { Category.Water }
    private val categoryLight by literalToken("light") map { Category.Light }
    private val categoryTemperature by literalToken("temperature") map { Category.Temperature }
    private val categoryHumidity by literalToken("humidity") map { Category.Humidity }
    private val categoryLocation by literalToken("location") map { Category.Location }

    private val category: Parser<Category> by categorySeed
        .or(categorySoil)
        .or(categoryFertilizer)
        .or(categoryWater)
        .or(categoryLight)
        .or(categoryTemperature)
        .or(categoryHumidity)
        .or(categoryLocation)

    private val seeds by parser {
        skip(seedPrefix)
        repeatZeroOrMore(long)
    }

    private val mapRange by parser {
        val destinationStart = long()
        val sourceStart = long()
        val length = long()

        MapRange(sourceStart, destinationStart, length)
    }

    private val map by parser {
        val source = category()
        skip(mapSeparator)
        val destination = category()
        skip(mapSuffix)

        val ranges = repeatZeroOrMore(mapRange)

        AlmanacMap(source, destination, ranges)
    }

    override val root by parser {
        val seeds = seeds()
        val maps = repeatZeroOrMore(map)

        Almanac(seeds, maps)
    }

}

