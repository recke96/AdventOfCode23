package com.github.recke96.aoc.days

import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

class Day08 : AoCCommand("day-8") {

    override val firstDemo = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent()

    override val secondDemo: String
        get() = TODO("Not yet implemented")

    override fun solveFirstPart(input: Sequence<String>): String {
        val doc = NavigationDocumentGrammar.parseOrThrow(input.joinToString("\n"))

        var steps = 0
        val instCount = doc.instructions.size
        var currentNode = Node("AAA")
        while (currentNode != Node("ZZZ")) {
            val instruction = doc.instructions[steps % instCount]
            steps++
            currentNode = doc.network[currentNode]?.select(instruction)
                ?: throw IllegalStateException("Can't find node $currentNode")
        }

        return steps.toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        TODO("Not yet implemented")
    }
}

enum class Direction { Left, Right }

@JvmInline
value class Node(private val id: String)

fun Pair<Node, Node>.select(dir: Direction): Node = when (dir) {
    Direction.Left -> first
    Direction.Right -> second
}

data class NavigationDocument(
    val instructions: List<Direction>,
    val network: Map<Node, Pair<Node, Node>>,
)

data

object NavigationDocumentGrammar : Grammar<NavigationDocument>() {

    init {
        // Skip whitespace
        regexToken("""\s+""", ignored = true)
    }

    private val eq by literalToken("=")
    private val opParen by literalToken("(")
    private val clParen by literalToken(")")
    private val comma by literalToken(",")

    private val direction: Parser<Direction> by (literalToken("L") map { Direction.Left })
        .or(literalToken("R") map { Direction.Right })

    private val node: Parser<Node> by regexToken("[A-Z]{3}") map { Node(it.text) }
    private val nodeNeighbors: Parser<Pair<Node, Pair<Node, Node>>> by parser {
        val node = node()
        skip(eq)
        skip(opParen)
        val left = node()
        skip(comma)
        val right = node()
        skip(clParen)

        node to (left to right)
    }

    override val root: Parser<NavigationDocument> by parser {
        val directions = repeatOneOrMore(direction)
        val network = repeatOneOrMore(nodeNeighbors)

        NavigationDocument(
            directions,
            network.toMap()
        )
    }
}


