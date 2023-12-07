package com.github.recke96.aoc.days

import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

class Day07() : AoCCommand("day-7") {

    override val firstDemo = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483    
    """.trimIndent()

    override val secondDemo = firstDemo

    override fun solveFirstPart(input: Sequence<String>): String {
        return input
                .map { HandAndBidGrammar.parseOrThrow(it) }
                .sortedByDescending { it.hand }
                .mapIndexed { rank, hb -> hb.bid * (rank + 1) }
                .sum()
                .toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String = TODO()
}

enum class Card {
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Queen,
    King,
    Ace
}

data class Hand(val cards: List<Card>) : Comparable<Hand> {

    enum class Type {
        HighCard,
        OnePair,
        TwoPair,
        ThreeOfAKind,
        FullHouse,
        FourOfAKind,
        FiveOfAKind
    }

    val type: Type

    init {
        require(cards.size == 5) {
            "A hand of cards consists of 5 cards, but contains ${cards.size}"
        }
        val cardCounts = cards.groupingBy { it }.eachCount().values
        type =
                when {
                    cardCounts.contains(5) -> Type.FiveOfAKind
                    cardCounts.contains(4) -> Type.FourOfAKind
                    cardCounts.contains(3) && cardCounts.contains(2) -> Type.FullHouse
                    cardCounts.count { it == 2 } == 2 -> Type.TwoPair
                    cardCounts.contains(2) -> Type.OnePair
                    else -> Type.HighCard
                }
    }

    companion object {
        val comparator =
                compareBy<Hand> { it.type }
                        .thenBy { it.cards[0] }
                        .thenBy { it.cards[1] }
                        .thenBy { it.cards[2] }
                        .thenBy { it.cards[3] }
                        .thenBy { it.cards[4] }
                        .thenBy { it.cards[5] }
    }

    override operator fun compareTo(other: Hand): Int = comparator.compare(this, other)
}

data class HandAndBid(val hand: Hand, val bid: Int)

data object HandAndBidGrammar : Grammar<HandAndBid>() {

    init {
        // Skip whitespace
        regexToken("""\s+""", ignored = true)
    }

    private val int: Parser<Int> by regexToken("""\d+""") map { it.text.toInt() }
    private val card: Parser<Card> by
            (literalToken("2") map { Card.Two })
                    .or(literalToken("3") map { Card.Three })
                    .or(literalToken("4") map { Card.Four })
                    .or(literalToken("5") map { Card.Five })
                    .or(literalToken("6") map { Card.Six })
                    .or(literalToken("7") map { Card.Seven })
                    .or(literalToken("8") map { Card.Eight })
                    .or(literalToken("9") map { Card.Nine })
                    .or(literalToken("T") map { Card.Ten })
                    .or(literalToken("J") map { Card.Jack })
                    .or(literalToken("Q") map { Card.Queen })
                    .or(literalToken("K") map { Card.King })
                    .or(literalToken("A") map { Card.Ace })

    private val hand: Parser<Hand> by repeated(card, atLeast = 5, atMost = 5) map { Hand(it) }

    override val root by parser {
        val hand = hand()
        val bid = int()

        HandAndBid(hand, bid)
    }
}
