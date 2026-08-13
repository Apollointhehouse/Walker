package dev.apollointhehouse.walker.level

interface Level {
    val tileSize: Int
    val size: Int
    val mapX: Int
    val mapY: Int

    fun get(x: Int, y: Int): Int
    fun getRaw(col: Int, row: Int): Int
}