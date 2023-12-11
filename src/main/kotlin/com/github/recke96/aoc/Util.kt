package com.github.recke96.aoc

fun <T1, T2> Iterable<T1>.cartesianProduct(other: Iterable<T2>): Set<Pair<T1, T2>> = flatMap { outer ->
    other.map { inner -> outer to inner }
}.toSet()
