package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.level.tile.Tile

interface Level {
    val entities: List<Entity>
    val tileSize: Int
    val size: Int
    val mapX: Int
    val mapY: Int

    fun get(x: Int, y: Int): Tile
    fun getRaw(col: Int, row: Int): Tile
}