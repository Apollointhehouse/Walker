package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.level.tile.TileAir
import dev.apollointhehouse.walker.level.tile.TilePosc
import dev.apollointhehouse.walker.level.tile.TileType
import dev.apollointhehouse.walker.level.tile.Tiles
import dev.apollointhehouse.walker.render.Renderer
import dev.apollointhehouse.walker.serialization.JsonLevelModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.logging.log4j.kotlin.logger
import org.joml.Vector2d
import org.joml.Vector2i
import java.io.InputStream

class JsonLevel(val model: JsonLevelModel) : MutableLevel {
    override val entities: List<Entity> field = mutableListOf()
    override val size get() = model.xSize * model.ySize
    override val mapX get() = model.xSize
    override val mapY get() = model.ySize
    override val tileSize get() = 64

    override fun getEntities(tilePos: TilePosc): List<Entity> {
        val tileMin = Vector2d(tilePos.x * tileSize.toDouble(), tilePos.y * tileSize.toDouble())
        val tileMax = Vector2d(tileMin.x() + tileSize, tileMin.y() + tileSize)

        return entities.filter { entity ->
            val eBB = entity.bb + entity.pos
            eBB.minX() < tileMax.x() && eBB.maxX() > tileMin.x() &&
                    eBB.minY() < tileMax.y() && eBB.maxY() > tileMin.y()
        }
    }

    override fun getType(tilePosc: TilePosc): TileType {
        if (tilePosc.x !in 0..<mapX || tilePosc.y !in 0..<mapY) return TileAir

        return Tiles.get(model.data[tilePosc.y * mapX + tilePosc.x]) ?: TileAir
    }

    override fun get(x: Int, y: Int): TilePosc {
        return TilePos(x, y)
    }

    override fun getType(x: Int, y: Int): TileType {
        return getType(TilePos(x, y))
    }

    fun get(i: Int): TilePos {
        val col = i % mapX
        val row = i / mapX

        return TilePos(Vector2i(col, row))
    }

    override fun addEntity(entity: Entity) {
        Renderer.addDrawable(entity)
        entities.add(entity)
        entity.level = this
    }

    companion object {
        private val json = Json { prettyPrint = true }

        @OptIn(ExperimentalSerializationApi::class)
        fun load(name: String): JsonLevel {
            val stream: InputStream? = javaClass.getResourceAsStream("/assets/levels/$name")
            if (stream == null) {
                logger.error("Level $name not found!")
                throw IllegalStateException("Failed to load json level $name")
            }

            val model = json.decodeFromStream<JsonLevelModel>(stream)
            return JsonLevel(model)
        }
    }
}