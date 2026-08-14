package dev.apollointhehouse.walker.level.tile

import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.level.Level
import org.joml.Vector2i

class LevelTile(
    val level: Level,
    override val position: Vector2i,
    override val type: Int
) : Tile {
    override val entities: List<Entity> get() = level.entities
        .filter { it.x.toInt() == position.x && it.y.toInt() == position.y }
}