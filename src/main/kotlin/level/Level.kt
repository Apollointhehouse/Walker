package dev.apollointhehouse.walker.level

interface Level {
    val size: Int
    val mapX: Int
    val mapY: Int

    fun get(x: Int, y: Int): Int
}