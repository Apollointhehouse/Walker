package dev.apollointhehouse.walker.level.tile

import org.joml.Vector2i

class TilePos(
    override val position: Vector2i,
) : TilePosc {
    constructor(x: Int, y: Int) : this(Vector2i(x, y))

    override val x get() = position.x
    override val y get() = position.y

//    override val entities: List<Entity> get() {
//        val tileMin = Vector2d(position.x() * level.tileSize.toDouble(), position.y() * level.tileSize.toDouble())
//        val tileMax = Vector2d(tileMin.x() + level.tileSize, tileMin.y() + level.tileSize)
//
//        return level.entities.filter { entity ->
//            val eBB = entity.bb + entity.position
//            eBB.minX() < tileMax.x() && eBB.maxX() > tileMin.x() &&
//                    eBB.minY() < tileMax.y() && eBB.maxY() > tileMin.y()
//        }
//    }
}