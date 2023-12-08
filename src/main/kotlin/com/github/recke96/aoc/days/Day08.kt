package com.github.recke96.aoc.days

import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken
import java.util.concurrent.ConcurrentHashMap

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

    override val secondDemo = """
        LR

        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
    """.trimIndent()

    override fun solveFirstPart(input: Sequence<String>): String {
        val doc = NavigationDocumentGrammar.parseOrThrow(input.joinToString("\n"))
        val instructions = sequence {
            while (true) {
                yieldAll(doc.instructions)
            }
        }
        var found = false
        var steps = 0L
        ghost(
            Node("AAA"),
            instructions,
            doc.network,
            { it == Node("ZZZ") },
            { _, s -> steps = s; found = true },
            { found }
        )

        return steps.toString()
    }

    override fun solveSecondPart(input: Sequence<String>): String {
        val doc = NavigationDocumentGrammar.parseOrThrow(input.joinToString("\n"))
        val inst = sequence {
            while (true) {
                yieldAll(doc.instructions)
            }
        }

        var record = 0
        var requiredSteps = -1L
        var stopped = false
        val startNodes = doc.network.keys.filter { it.endsWith("A") }
        val isTarget = { current: Node -> current.endsWith("Z") }
        val targets = ConcurrentHashMap<Long, List<Node>>()
        val onTargetFound = { target: Node, steps: Long ->
            val targetsOfStep = targets.merge(steps, listOf(target), List<Node>::plus)
            val matchedTargets = targetsOfStep?.size ?: 0
            if (matchedTargets == startNodes.size) {
                requiredSteps = steps
                stopped = true
            }
            if (matchedTargets > record) {
                record = matchedTargets
                println("New record: ${targetsOfStep?.joinToString()} nodes end at $steps")
            }
        }
        val isStopped = { stopped }
        val threads = startNodes.map {
            Thread { ghost(it, inst, doc.network, isTarget, onTargetFound, isStopped) }.apply { start() }
        }

        println("Searching for ${startNodes.size} *Z nodes")

        threads.forEach(Thread::join)

        return requiredSteps.toString()
    }

    private fun ghost(
        start: Node,
        instructions: Sequence<Direction>,
        network: Map<Node, Pair<Node, Node>>,
        isTarget: (Node) -> Boolean,
        onTargetFound: (Node, Long) -> Unit,
        isStopped: () -> Boolean
    ) {
        var steps = 0L
        var current = start
        for (inst in instructions) {
            if (isStopped()) {
                break
            }
            if (isTarget(current)) {
                onTargetFound(current, steps)
            }

            steps++
            current = network[current]?.select(inst)
                ?: throw IllegalStateException("No node in network for $current")
        }
    }
}

enum class Direction { Left, Right }

@JvmInline
value class Node(private val id: String) {
    fun endsWith(suffix: String): Boolean = id.endsWith(suffix)
}

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

    private val node: Parser<Node> by regexToken("[A-Z0-9]{3}") map { Node(it.text) }
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


