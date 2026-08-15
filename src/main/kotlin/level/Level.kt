package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.level.tile.TilePosc
import dev.apollointhehouse.walker.level.tile.TileType

interface Level {
    val entities: List<Entity>
    val tileSize: Int
    val size: Int
    val mapX: Int
    val mapY: Int

    fun getEntities(tilePos: TilePosc): List<Entity>

    fun get(x: Int, y: Int): TilePosc
    fun getType(x: Int, y: Int): TileType

    fun getType(tilePosc: TilePosc): TileType
}