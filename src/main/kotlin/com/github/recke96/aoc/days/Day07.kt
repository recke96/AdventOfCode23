package com.github.recke96.aoc.days

import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken
import java.util.Comparator.comparing

class Day07() : AoCCommand("day-7") {

    override val firstDemo = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483    
    """.trimIndent()

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String = calculateTotalWinnings(input, Hand.defaultComparator)

    override fun solveSecondPart(input: Sequence<String>): String = calculateTotalWinnings(input, Hand.jokerComparator)

    private fun calculateTotalWinnings(input: Sequence<String>, comparator: Comparator<Hand>): String =
        input.map { HandAndBidGrammar.parseOrThrow(it) }
            .sortedWith(comparing(HandAndBid::hand, comparator))
            .mapIndexed { rank, hb -> hb.bid * (rank + 1) }
            .sum()
            .toString()
}

enum class Card {
    Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace;

    companion object {
        val defaultComparator = Comparator(Card::compareTo)
        val jokerComparator = Comparator<Card> { a, b ->
            // with joker Jack is the least valuable card
            when {
                a == b -> 0
                a == Jack -> -1
                b == Jack -> 1
                else -> defaultComparator.compare(a, b)
            }
        }
    }
}

data class Hand(val cards: List<Card>) {

    enum class Type {
        HighCard, OnePair, TwoPair, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind
    }

    val type: Type
    val typeUsingJokers: Type

    init {
        require(cards.size == 5) {
            "A hand of cards consists of 5 cards, but contains ${cards.size}"
        }

        type = findHandType(cards)
        typeUsingJokers = findJokerHandType(cards)
    }

    companion object {
        private fun buildComparatorUsing(
            typeExtractor: (Hand) -> Type, cardComparator: Comparator<Card>
        ): Comparator<Hand> {
            var comp = compareBy(typeExtractor)
            for (i in 0..4) {
                comp = comp.thenComparing({ it.cards[i] }, cardComparator)
            }
            return comp
        }

        val defaultComparator = buildComparatorUsing(Hand::type, Card.defaultComparator)
        val jokerComparator = buildComparatorUsing(Hand::typeUsingJokers, Card.jokerComparator)

        private fun findHandType(cards: List<Card>): Type {
            val cardCounts = cards.groupingBy { it }.eachCount().values.sortedDescending()
            val most = cardCounts.first()
            val second = cardCounts.getOrElse(1) { 0 }
            return when (most) {
                5 -> Type.FiveOfAKind
                4 -> Type.FourOfAKind
                3 -> if (second == 2) Type.FullHouse else Type.ThreeOfAKind
                2 -> if (second == 2) Type.TwoPair else Type.OnePair
                else -> Type.HighCard
            }
        }

        private fun findJokerHandType(cards: List<Card>): Type {
            val cardCounts = cards.groupingBy { it }.eachCount()
            val jokers = cardCounts[Card.Jack] ?: 0
            val sorted = cardCounts.filterKeys { it != Card.Jack }.values.sortedDescending()
            val most = sorted.getOrElse(0) { 0 }
            val second = sorted.getOrElse(1) { 0 }
            return when (most + jokers) {
                5 -> Type.FiveOfAKind
                4 -> Type.FourOfAKind
                3 -> if (second == 2) Type.FullHouse else Type.ThreeOfAKind
                2 -> if (second == 2) Type.TwoPair else Type.OnePair
                else -> Type.HighCard
            }
        }
    }
}

data class HandAndBid(val hand: Hand, val bid: Int)

data object HandAndBidGrammar : Grammar<HandAndBid>() {

    init {
        // Skip whitespace
        regexToken("""\s+""", ignored = true)
    }

    private val int: Parser<Int> by regexToken("""\d+""") map { it.text.toInt() }
    private val card: Parser<Card> by (literalToken("2") map { Card.Two }).or(literalToken("3") map { Card.Three })
        .or(literalToken("4") map { Card.Four }).or(literalToken("5") map { Card.Five })
        .or(literalToken("6") map { Card.Six }).or(literalToken("7") map { Card.Seven })
        .or(literalToken("8") map { Card.Eight }).or(literalToken("9") map { Card.Nine })
        .or(literalToken("T") map { Card.Ten }).or(literalToken("J") map { Card.Jack })
        .or(literalToken("Q") map { Card.Queen }).or(literalToken("K") map { Card.King })
        .or(literalToken("A") map { Card.Ace })

    private val hand: Parser<Hand> by repeated(card, atLeast = 5, atMost = 5) map { Hand(it) }

    override val root by parser {
        val hand = hand()
        val bid = int()

        HandAndBid(hand, bid)
    }
}
