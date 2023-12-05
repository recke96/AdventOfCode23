package com.github.recke96.aoc

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.recke96.aoc.days.*

class AdventOfCode() : CliktCommand() {
    init {
        subcommands(
            Day01(),
            Day02(),
            Day03(),
            Day04(),
            Day05(),
        )
    }

    override fun run() = Unit
}

fun main(args: Array<String>) = AdventOfCode().main(args)
