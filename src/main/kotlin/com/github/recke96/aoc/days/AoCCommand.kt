package com.github.recke96.aoc.days

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.inputStream
import java.io.InputStreamReader

enum class AocQuizPart { One, Two }

abstract class AoCCommand(name: String) : CliktCommand(name = name) {

    private val demo: Boolean by option("--demo", "-d").flag(default = false)
    private val part: AocQuizPart by option("--part", "-p").enum<AocQuizPart>().default(AocQuizPart.One)
    private val input: InputStreamReader? by argument().inputStream().convert { it.reader() }.optional()

    abstract val firstDemo: String
    abstract val secondDemo: String
    private val demoInput: String
        get() = when (part) {
            AocQuizPart.One -> firstDemo
            AocQuizPart.Two -> secondDemo
        }

    abstract fun solveFirstPart(input: String): String
    abstract fun solveSecondPart(input: String): String

    override fun run() {
        val input = getInput()
        val solution = when(part){
            AocQuizPart.One -> solveFirstPart(input)
            AocQuizPart.Two -> solveSecondPart(input)
        }

        println("Solution $solution")
    }

    private fun getInput(): String {
        input.use { input ->
            return if (demo) {
                demoInput
            } else {
                input?.readText() ?: throw UsageError("input is required when --demo is not set")
            }
        }
    }
}
