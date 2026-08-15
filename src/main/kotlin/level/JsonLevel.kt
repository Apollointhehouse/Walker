package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.level.tile.TileAir
import dev.apollointhehouse.walker.level.tile.TilePosc
import dev.apollointhehouse.walker.level.tile.TileType
import dev.apollointhehouse.walker.level.tile.Tiles
import dev.apollointhehouse.walker.serialization.JsonLevelModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.logging.log4j.kotlin.logger
import org.joml.Vector2i
import java.io.InputStream

class JsonLevel(val model: JsonLevelModel) : Level {
    override val entities: List<Entity> = emptyList()
    override val size get() = model.xSize * model.ySize
    override val mapX get() = model.xSize
    override val mapY get() = model.ySize
    override val tileSize get() = 64

    override fun getEntities(tilePos: TilePosc): List<Entity> = entities
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