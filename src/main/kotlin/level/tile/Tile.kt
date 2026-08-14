package dev.apollointhehouse.walker.level.tile

import dev.apollointhehouse.walker.entity.Entity
import org.joml.Vector2i

interface Tile {
    val type: Int
    val position: Vector2i
    val entities: List<Entity>
}