package com.github.recke96.aoc.days

import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken
import java.util.Comparator.comparingInt

class Day04 : AoCCommand("day-4") {

    override val firstDemo = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent()

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String =
        input.map { ScratchcardGrammar.parseOrThrow(it) }
            .map { it.score() }
            .sum()
            .toString()

    override fun solveSecondPart(input: Sequence<String>): String {
        val matchesPerCard = input.map { ScratchcardGrammar.parseOrThrow(it) }
            .toSortedSet(comparingInt(Scratchcard::id))

        val copiesOfCard = matchesPerCard
            .map(Scratchcard::id)
            .associateWith { 1 }
            .toMutableMap()
        var totalCards = 0

        for (card in matchesPerCard) {
            val cardCount = copiesOfCard[card.id]!!
            totalCards += cardCount

            val matches = card.matches()
            if (matches == 0) {
                continue
            }

            for (cardToCopy in (card.id + 1)..(card.id + matches)) {
                copiesOfCard.computeIfPresent(cardToCopy) { _, count -> count + cardCount }
            }
        }

        return totalCards.toString()
    }
}

data class Scratchcard(val id: Int, val winners: Set<Int>, val own: Set<Int>)

fun Scratchcard.matches(): Int = winners.fold(0) { matches, current ->
    if (own.contains(current)) matches + 1 else matches
}

fun Scratchcard.score() = matches().let { if (it > 0) 1 shl (it - 1) else 0 }

data object ScratchcardGrammar : Grammar<Scratchcard>() {

    init {
        // Ignore whitespace
        regexToken("""\s+""", ignored = true)
    }

    private val card by literalToken("Card")
    private val colon by literalToken(":")
    private val pipe by literalToken("|")
    private val int by regexToken("""\d+""") map { it.text.toInt() }

    private val id by parser {
        skip(card)
        val id = int()
        skip(colon)

        id
    }

    override val root by parser {
        val id = id()
        val winners = repeatZeroOrMore(int)
        skip(pipe)
        val own = repeatZeroOrMore(int)

        Scratchcard(id, winners.toSet(), own.toSet())
    }

}
