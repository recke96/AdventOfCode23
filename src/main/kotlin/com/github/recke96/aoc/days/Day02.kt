package com.github.recke96.aoc.days

import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

class Day02 : AoCCommand("day-2") {
    override val firstDemo = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent()

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String = input
        .filter { it.isNotBlank() }
        .map { GameGrammar.parseOrThrow(it) }
        .map { it.id to (it.draws.reduceOrNull(::max) ?: Draw(0, 0, 0)) }
        .filter { it.second.reds <= 12 }
        .filter { it.second.greens <= 13 }
        .filter { it.second.blues <= 14 }
        .sumOf { it.first }
        .toString()

    override fun solveSecondPart(input: Sequence<String>): String = input
        .filter { it.isNotBlank() }
        .map { GameGrammar.parseOrThrow(it) }
        .map { it.draws.reduceOrNull(::max) ?: Draw(0, 0, 0) }
        .map { it.power() }
        .sum()
        .toString()
}

data class Game(val id: Int, val draws: List<Draw>)
data class Draw(val reds: Int, val greens: Int, val blues: Int)

fun max(a: Draw, b: Draw) = Draw(
    kotlin.math.max(a.reds, b.reds),
    kotlin.math.max(a.greens, b.greens),
    kotlin.math.max(a.blues, b.blues)
)

fun Draw.power() = reds * greens * blues

object GameGrammar : Grammar<Game>() {

    init {
        // Ignore whitespace
        regexToken("""\s+""", ignored = true)
    }

    private enum class Color { Red, Green, Blue }

    private val gameLiteral by literalToken("Game")
    private val colonLiteral by literalToken(":")
    private val redLiteral by literalToken("red").map { Color.Red }
    private val greenLiteral by literalToken("green").map { Color.Green }
    private val blueLiteral by literalToken("blue").map { Color.Blue }

    private val intToken by regexToken("""\d+""").map { it.text.toInt() }

    private val gameId by parser {
        skip(gameLiteral)
        val id = intToken()
        skip(colonLiteral)

        id
    }

    private val drawSeparator by literalToken(",")
    private val drawTerminator by literalToken(";")

    private val colorCount by parser {
        val count = intToken()
        val color = choose(redLiteral, greenLiteral, blueLiteral)

        color to count
    }

    private val draw by parser {
        val drawnColors = mutableMapOf<Color, Int>()
        var isTerminated = false

        while (!isTerminated) {
            val (color, count) = colorCount()
            drawnColors.merge(color, count, Int::plus)

            val sep = maybe(drawSeparator or drawTerminator).map { it?.text }()
            if (sep == null || sep == ";") {
                isTerminated = true
            }
        }

        Draw(
            drawnColors[Color.Red] ?: 0,
            drawnColors[Color.Green] ?: 0,
            drawnColors[Color.Blue] ?: 0,
        )
    }
    override val root by parser {
        val id = gameId()
        val draws = repeatZeroOrMore(draw)

        Game(id, draws)
    }
}
