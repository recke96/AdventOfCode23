package com.github.recke96.aoc

fun IntRange.cartesianProduct(other: IntRange): Set<Pair<Int, Int>> = flatMap { outer ->
    other.map { inner -> outer to inner }
}.toSet()
