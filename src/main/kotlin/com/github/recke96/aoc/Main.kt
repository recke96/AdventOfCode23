package com.github.recke96.aoc

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.recke96.aoc.days.DayOne
import com.github.recke96.aoc.days.DayThree
import com.github.recke96.aoc.days.DayTwo

class AdventOfCode() : CliktCommand() {
    init {
        subcommands(
            DayOne(),
            DayTwo(),
            DayThree(),
        )
    }

    override fun run() = Unit
}

fun main(args: Array<String>) = AdventOfCode().main(args)
